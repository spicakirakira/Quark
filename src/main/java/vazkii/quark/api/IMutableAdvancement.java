package vazkii.quark.api;

import com.google.common.collect.Lists;
import net.minecraft.advancements.Criterion;

public interface IMutableAdvancement {

    void addRequiredCriterion(String name, Criterion criterion);

    void addOrCriterion(String name, Criterion criterion);

    Criterion getCriteria(String title);
}
