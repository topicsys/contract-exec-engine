package systems.topic.lee.lib;

import hobby.chenai.nakam.basis.TAG;
import systems.topic.lee.package$;

/**
 * @author Chenai Nakam(chenai.nakam@gmail.com)
 * @version 1.0, 14/08/2018
 */
public abstract class AbsCLoaderJ extends java.lang.ClassLoader {
    protected AbsCLoaderJ(java.lang.ClassLoader parent) {
        super(parent);
    }

    static {
        @SuppressWarnings("StaticInitializerReferencesSubClass")
        boolean par = ClassLoader.registerAsParallelCapable();
        package$.MODULE$.loggerJ().d(new TAG.LogTag(AbsCLoaderJ.class.getName()), "registerAsParallelCapable:%s.", par);
    }
}
