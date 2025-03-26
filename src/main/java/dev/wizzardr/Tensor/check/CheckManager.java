package dev.wizzardr.Tensor.check;

import dev.wizzardr.Tensor.check.factory.SwingCheck;
import dev.wizzardr.Tensor.data.PlayerData;
import lombok.Getter;
import org.atteo.classindex.ClassIndex;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

@Getter
public class CheckManager {
    private final Set<Class<? extends SwingCheck>> checkClasses = new HashSet<>();
    private final Set<Constructor<?>> constructors = new HashSet<>();

    public CheckManager() {
        ClassIndex.getSubclasses(SwingCheck.class, SwingCheck.class.getClassLoader())
                .forEach(clazz -> {

                    if (Modifier.isAbstract(clazz.getModifiers()))
                        return;

                    checkClasses.add(clazz);
                });

        checkClasses.forEach(clazz -> {
            try {
                constructors.add(clazz.getConstructor(PlayerData.class));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        });
    }
}