/*******************************************************************************
 * Copyright (c) 2017, 2018 Certiv Analytics. All rights reserved.
 * Use of this file is governed by the Eclipse Public License v1.0
 * that can be found in the LICENSE.txt file in the project root,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package net.certiv.adept.model.load;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;

import net.certiv.adept.Tool;
import net.certiv.adept.core.CoreMgr;
import net.certiv.adept.model.CorpusModel;
import net.certiv.adept.model.Feature;
import net.certiv.adept.tool.ErrorDesc;

public class CorpusData {

	public static final int OLD = 8;

	public static boolean validModelStore(CoreMgr mgr, Path corpusDir) {
		if (!Files.isDirectory(corpusDir)) return false;

		try {
			CorpusModel model = loadCorModel(mgr, archiveBuilder(), corpusDir);
			if (model == null) return false;

			List<Path> paths = getDataFiles(mgr.getTool(), corpusDir);
			if (paths.isEmpty()) return false;
			if (paths.size() != model.getPathnames().size()) return false;
			if (model.modified()) return false;

		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * Load the corpus model from persistent store. The model is stored in two parts: (1) the base model
	 * file; and (2) a plurality of feature set data files.
	 *
	 * @param mgr
	 * @param corpusDir directory to load from
	 * @param pathnames the pathnames of the corpus files expected to be included in the corpus model
	 * @return the corpus model
	 * @throws Exception if any persisted file cannot found or read
	 */
	public static CorpusModel loadModel(CoreMgr mgr, Path corpusDir, List<String> pathnames) throws Exception {
		Tool tool = mgr.getTool();
		if (!Files.isDirectory(corpusDir)) {
			tool.toolInfo(CorpusData.class, "Corpus directory does not exist: " + corpusDir.toString());
			throw new IOException("Corpus directory does not exist: " + corpusDir.toString());
		}

		Gson gson = archiveBuilder();
		CorpusModel corModel = loadCorModel(mgr, gson, corpusDir);
		corModel.setConsistent(false);

		List<Path> paths = getDataFiles(mgr.getTool(), corpusDir);
		if (paths.isEmpty()) return corModel;

		for (Path path : paths) {
			try {
				InputStream is = new GZIPInputStream(Files.newInputStream(path));
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
				FeatureSet featureSet = gson.fromJson(reader, FeatureSet.class);
				if (pathnames.contains(featureSet.getPathname())) {
					corModel.merge(featureSet);
				}
			} catch (IOException | JsonSyntaxException e) {
				tool.toolInfo(CorpusData.class, "Failed loading " + path.toString() + ": " + e.getMessage());
				throw e;
			}
		}
		corModel.setConsistent(true);
		return corModel;
	}

	public static CorpusModel loadCorModel(CoreMgr mgr, Gson gson, Path corpusDir) throws Exception {
		Tool tool = mgr.getTool();

		Path path = corpusDir.resolve(ConfigMgr.MODEL + ConfigMgr.DOT + ConfigMgr.EXT);
		if (!Files.isRegularFile(path)) return null;

		try {
			InputStream is = new GZIPInputStream(Files.newInputStream(path));
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			CorpusModel corModel = gson.fromJson(reader, CorpusModel.class);
			corModel.setMgr(mgr);
			corModel.setCorpusDir(corpusDir);
			return corModel;
		} catch (Exception e) {
			tool.toolInfo(CorpusData.class, "Failed loading model base " + path.toString() + ": " + e.getMessage());
			throw e;
		}
	}

	/** Remove all of the data files that represent the corpus. */
	public static boolean removeDataFiles(Tool tool, Path corpusDir) {
		boolean successful = true;
		for (Path datafile : getDataFiles(tool, corpusDir)) {
			successful = successful && datafile.toFile().delete();
		}
		return successful;
	}

	/* Returns a list of the corpus data file pathnames. */
	public static List<Path> getDataFiles(Tool tool, Path corpusDir) {
		try {
			return Files.walk(corpusDir, 1) //
					.filter(Files::isRegularFile) //
					.filter(p -> p.getFileName().toString().startsWith(ConfigMgr.DATA)) //
					.filter(p -> p.getFileName().toString().endsWith(ConfigMgr.DOT + ConfigMgr.EXT)) //
					.collect(Collectors.toList());
		} catch (IOException e) {
			tool.toolError(CorpusData.class, ErrorDesc.CANNOT_OPEN_FILE, e, "Failed reading Data file names");
		}
		return Collections.emptyList();
	}

	private static Gson archiveBuilder() {
		GsonBuilder builder = new GsonBuilder();
		builder.enableComplexMapKeySerialization() //
				.disableHtmlEscaping() //
				.enableComplexMapKeySerialization() //
				.excludeFieldsWithoutExposeAnnotation() //
				// .registerTypeAdapter(TreeMultiset.class, new TreeMultimapAdapter<Integer, Aspect>()) //
				// .registerTypeAdapter(TreeMultiset.class, new TreeMultimapAdapter<Integer, Adjunct>()) //
				.serializeNulls() //
				.setDateFormat(DateFormat.LONG) //
				.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY) //
				.setPrettyPrinting();
		return builder.create();
	}

	/** Configure writer for 2-space indents */
	private static JsonWriter archiveWriter(OutputStream out) throws IOException {
		JsonWriter writer = new JsonWriter(new BufferedWriter(new OutputStreamWriter(out, "UTF-8")));
		writer.setIndent("  ");
		return writer;
	}

	/**
	 * Saves the corpus model to a Json file in the given directory. Overwrites any existing file. Saves
	 * in a compact group.
	 *
	 * @param corpusDir directory to save to
	 * @throws Exception if an existing file cannot be overwritten
	 */
	public static void save(CorpusModel model, Path corpusDir) throws Exception {
		save(corpusDir, model, true);
	}

	/**
	 * Saves the corpus model to a Json file in the given directory. Overwrites any existing file.
	 * <p>
	 * Saves the exposed fields of the CorpusModel in a primary file. Saves the features of each of the
	 * corpus documents in corresponding secondary files.
	 *
	 * @param corpusDir directory to save to
	 * @param inclDocs whether to also save the corpus document data files
	 * @throws Exception if an existing file cannot be overwritten
	 */
	public static void save(Path corpusDir, CorpusModel model, boolean inclDocs) throws Exception {
		Tool tool = model.getMgr().getTool();
		Gson gson = archiveBuilder();

		Path path = corpusDir.resolve(ConfigMgr.MODEL + ConfigMgr.DOT + ConfigMgr.EXT);
		JsonWriter writer = archiveWriter(new GZIPOutputStream(Files.newOutputStream(path)));

		try {
			gson.toJson(model, CorpusModel.class, writer);
			writer.close();
		} catch (IOException | JsonSyntaxException e) {
			tool.toolInfo(CorpusData.class,
					"Failed saving corpus model file " + path.toString() + ": " + e.getMessage(), e);
			throw e;
		}
		tool.toolInfo(CorpusData.class, "Saved " + path.getFileName().toString());

		if (inclDocs) {
			int idx = 0;
			for (int docId : model.getDocFeatures().keySet()) {
				String pathname = model.getPathname(docId);
				List<Feature> features = model.getDocFeatures().get(docId);

				path = corpusDir.resolve(String.format("%s%03d%s", ConfigMgr.DATA, idx, ConfigMgr.DOT + ConfigMgr.EXT));
				try {
					writer = archiveWriter(new GZIPOutputStream(Files.newOutputStream(path)));
				} catch (IOException e) {
					tool.toolInfo(CorpusData.class,
							"Failed accessing corpus data file " + path.toString() + ": " + e.getMessage(), e);
					throw e;
				}

				try {
					gson.toJson(new FeatureSet(docId, pathname, features), FeatureSet.class, writer);
					writer.close();
				} catch (IOException | JsonSyntaxException e) {
					tool.toolInfo(CorpusData.class,
							"Failed saving corpus data file " + path.toString() + ": " + e.getMessage(), e);
					throw e;
				}
				idx++;
			}
			tool.toolInfo(ConfigMgr.class, "Saved " + idx + " data files");
		}
	}

	// // Google Multimap
	// private static final class MultimapAdapter<K, V>
	// implements JsonSerializer<Multimap<K, V>>, JsonDeserializer<Multimap<K, V>> {
	//
	// private static final Type retType;
	// static {
	// try {
	// retType = Multimap.class.getDeclaredMethod("asMap").getGenericReturnType();
	// } catch (NoSuchMethodException e) {
	// throw new AssertionError(e);
	// }
	// }
	//
	// @Override
	// public JsonElement serialize(Multimap<K, V> src, Type typeOfSrc, JsonSerializationContext
	// context) {
	// return context.serialize(src.asMap(), asMapType(typeOfSrc));
	// }
	//
	// @Override
	// public Multimap<K, V> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext
	// context)
	// throws JsonParseException {
	// Map<K, Collection<V>> asMap = context.deserialize(json, asMapType(typeOfT));
	// Multimap<K, V> multimap = ArrayListMultimap.create();
	// for (Map.Entry<K, Collection<V>> entry : asMap.entrySet()) {
	// multimap.putAll(entry.getKey(), entry.getValue());
	// }
	// return multimap;
	// }
	//
	// private static Type asMapType(Type multimapType) {
	// return TypeToken.of(multimapType).resolveType(retType).getType();
	// }
	// }

	// private static final class HashTreeAdapter<K, V>
	// implements JsonSerializer<HashTreeSet<K, V>>, JsonDeserializer<HashTreeSet<K, V>> {
	//
	// private static final Type retType;
	// static {
	// try {
	// retType = HashTreeSet.class.getDeclaredMethod("asMap").getGenericReturnType();
	// } catch (NoSuchMethodException e) {
	// throw new AssertionError(e);
	// }
	// }
	//
	// @Override
	// public JsonElement serialize(HashTreeSet<K, V> src, Type typeOfSrc, JsonSerializationContext
	// context) {
	// return context.serialize(src.asMap(), asMapType(typeOfSrc));
	// }
	//
	// @Override
	// public HashTreeSet<K, V> deserialize(JsonElement json, Type typeOfT,
	// JsonDeserializationContext context)
	// throws JsonParseException {
	//
	// Map<K, TreeSet<V>> asMap = context.deserialize(json, asMapType(typeOfT));
	// return new HashTreeSet<K, V>(asMap);
	// }
	//
	// private static Type asMapType(Type multimapType) {
	// return TypeToken.of(multimapType).resolveType(retType).getType();
	// }
	// }

}
