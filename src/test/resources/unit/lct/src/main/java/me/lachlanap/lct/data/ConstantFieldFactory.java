package me.lachlanap.lct.data;

import java.util.ArrayList;
import java.util.List;
import me.lachlanap.lct.Constant;
import me.lachlanap.lct.ConstantException;
import me.lachlanap.lct.spi.ConstantFieldProvider;
import me.lachlanap.lct.spi.impl.PrimitivesProvider;

/**
 *
 * @author lachlan
 */
public class ConstantFieldFactory {

    private final List<ConstantFieldProvider> providers;

    public ConstantFieldFactory() {
        providers = new ArrayList<>();
        providers.add(new PrimitivesProvider());
    }

    public void addProvider(ConstantFieldProvider provider) {
        providers.add(provider);
    }

    public ConstantField createConstantField(Class<?> container, String field, Constant annot) {
        Class<?> type;

        try {
            type = container.getField(field).getType();
        } catch (NoSuchFieldException e) {
            throw new ConstantException("Cannot find constant " + annot.name()
                    + " (" + container.getSimpleName() + "." + field + ")", e);
        }

        for (ConstantFieldProvider provider : providers) {
            if (provider.canProvide(type))
                return provider.getField(type, container, field, annot);
        }

        throw new ConstantException("Cannot setup constant " + annot.name()
                + " of type " + type.getSimpleName()
                + "; no provider");
    }
}
