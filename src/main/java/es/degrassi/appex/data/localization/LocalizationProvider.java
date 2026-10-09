package es.degrassi.appex.data.localization;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import es.degrassi.appex.AppliedExperienced;
import es.degrassi.experiencelib.api.datagen.LocalizationEnum;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

public abstract class LocalizationProvider implements DataProvider {
  public static List<LocalizationProvider> create(DataGenerator generator) {
    return List.of(
        new EnUsLangProvider(generator),
        new EsEsLangProvider(generator),
        new JaJpLangProvider(generator),
        new RuRuLangProvider(generator),
        new ZhCnLangProvider(generator)
    );
  }
  private final Map<String, String> localizations = new HashMap<>();

  private final DataGenerator generator;
  private boolean wasSaved = false;

  protected LocalizationProvider(DataGenerator generator) {
    this.generator = generator;
  }

  public abstract Lang getLang();

  public final @NonNull CompletableFuture<?> run(@NonNull CachedOutput cache) {
    generate();
    return save(cache, localizations);
  }

  public LocalizationProvider withTranslations(Map<String, String> translations) {
    if (translations != null && !translations.isEmpty()) localizations.putAll(translations);
    return this;
  }

  @Override
  public @NonNull String getName() {
    return "Localization (%s)".formatted(getLang().name());
  }

  public <T extends Enum<T> & LocalizationEnum> void addEnum(Class<T> localizedEnum) {
    addEnum(localizedEnum, LocalizationEnum::getEnglishText);
  }

  public <T extends Enum<T> & LocalizationEnum> void addEnum(Class<T> localizedEnum, Function<@NotNull T, String> parser) {
    for (var enumConstant : localizedEnum.getEnumConstants()) {
      var parsedResult = parser.apply(enumConstant);
      if (parsedResult == null) continue;
      add(enumConstant.getTranslationKey(), parsedResult);
    }
  }

  protected final void add(TagKey<?> tagKey, String name) {
    this.add(Tags.getTagTranslationKey(tagKey), name);
  }

  public void add(String key, String text) {
    Preconditions.checkState(!wasSaved, "Cannot add more translations after they were already saved");
    var previous = localizations.put(key, text);
    if (previous != null) {
      throw new IllegalStateException("Localization key " + key + " is already translated to: " + previous);
    }
  }

  protected abstract void generate();

  private CompletableFuture<?> save(CachedOutput cache, Map<String, String> localizations) {
    wasSaved = true;

    var path = this.generator.getPackOutput().getOutputFolder().resolve("assets/%s/lang/%s.json".formatted(AppliedExperienced.MODID, getLang().name()));

    // Dump the translation in ascending order
    var sorted = new TreeMap<>(localizations);
    var jsonLocalization = new JsonObject();
    for (var entry : sorted.entrySet()) {
      jsonLocalization.addProperty(entry.getKey(), entry.getValue());
    }

    return DataProvider.saveStable(cache, jsonLocalization, path);
  }
}
