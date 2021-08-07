package yaml.mc.commentedyaml;

import java.util.function.Function;

/**
 * Provides the comments list for a given path in a {@link CommentedYamlConfiguration} file.
 */
public interface CommentsProvider extends Function<String, String[]> {}
