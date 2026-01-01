package dev.slne.surf.event.buildit.manager.plot;

import com.plotsquared.core.configuration.caption.StaticCaption;
import com.plotsquared.core.plot.flag.FlagParseException;
import com.plotsquared.core.plot.flag.InternalFlag;
import com.plotsquared.core.plot.flag.PlotFlag;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import dev.slne.surf.event.buildit.manager.plot.CategoryFlag.Category;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class CategoryFlag extends PlotFlag<Category, CategoryFlag> implements InternalFlag {

  private static final Object2ObjectMap<Category, CategoryFlag> VALUES = new Object2ObjectOpenHashMap<>(
      7);

  public static final CategoryFlag CATEGORY_NOT_SET = create(Category.NOT_SET);
  public static final CategoryFlag CATEGORY_20 = create(Category._20);
  public static final CategoryFlag CATEGORY_50 = create(Category._50);
  public static final CategoryFlag CATEGORY_100 = create(Category._100);
  public static final CategoryFlag CATEGORY_250 = create(Category._250);
  public static final CategoryFlag CATEGORY_500 = create(Category._500);

  private static @NotNull CategoryFlag create(@NonNull Category value) {
    final CategoryFlag flag = new CategoryFlag(value);
    VALUES.put(value, flag);
    return flag;
  }

  public static CategoryFlag getByCategory(@NonNull Category category) {
    return VALUES.get(category);
  }

  public static List<CategoryFlag> getAllSetCategories() {
    return List.of(CATEGORY_20, CATEGORY_50, CATEGORY_100, CATEGORY_250, CATEGORY_500);
  }

  /**
   * Construct a new flag instance.
   *
   * @param value Flag value
   */
  protected CategoryFlag(final @NonNull Category value) {
    super(value, StaticCaption.of("Category"), StaticCaption.of("The category of the plot"));
  }

  @Override
  public CategoryFlag parse(@NonNull String input) throws FlagParseException {
    try {
      return new CategoryFlag(Category.valueOf(input));
    } catch (IllegalArgumentException e) {
      throw new FlagParseException(this, input, StaticCaption.of("Invalid category: " + input));
    }
  }

  @Override
  public CategoryFlag merge(@NonNull Category newValue) {
    return flagOf(newValue);
  }

  @Override
  public String toString() {
    return getValue().name();
  }

  @Override
  public String getExample() {
    return Category._20.name();
  }

  @Override
  protected CategoryFlag flagOf(@NonNull Category value) {
    return switch (value) {
      case _20 -> CATEGORY_20;
      case _50 -> CATEGORY_50;
      case _100 -> CATEGORY_100;
      case _250 -> CATEGORY_250;
      case _500 -> CATEGORY_500;
      default -> CATEGORY_NOT_SET;
    };
  }

  public enum Category {
    _20("20\u20AC", BlockTypes.COPPER_BLOCK),
    _50("50\u20AC", BlockTypes.IRON_BLOCK),
    _100("100\u20AC", BlockTypes.GOLD_BLOCK),
    _250("250\u20AC", BlockTypes.DIAMOND_BLOCK),
    _500("500\u20AC", BlockTypes.EMERALD_BLOCK),
    NOT_SET("/", BlockTypes.WHITE_CONCRETE);

    private final String name;
    private final BlockType blockType;

    Category(String name, BlockType blockType) {
      this.name = name;
      this.blockType = blockType;
    }

    public CategoryFlag getFlag() {
      return CategoryFlag.getByCategory(this);
    }

    public String getDisplayName() {
      return this.name;
    }

    public static List<Category> getAsList() {
      return List.of(_20, _50, _100, _250, _500);
    }

    public BlockType getBlockType() {
      return blockType;
    }
  }
}
