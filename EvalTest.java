/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//package evaltest;
//import static evaltest.DynamicCompiler.compile;
//import static evaltest.DynamicCompiler.runIt;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Locale;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 *
 * @author Simon Mages <mages.simon@googlemail.com>
 */
public class EvalTest {

    /**
     * @param args the command line arguments
     */
    private static String classOutputFolder = "/tmp";

    public static class MyDiagnosticListener implements DiagnosticListener<JavaFileObject> {

        public void report(Diagnostic<? extends JavaFileObject> diagnostic) {

            System.out.println("Line Number->" + diagnostic.getLineNumber());
            System.out.println("code->" + diagnostic.getCode());
            System.out.println("Message->"
                    + diagnostic.getMessage(Locale.ENGLISH));
            System.out.println("Source->" + diagnostic.getSource());
            System.out.println(" ");
        }
    }

    public static class InMemoryJavaFileObject extends SimpleJavaFileObject {

        private String contents = null;

        public InMemoryJavaFileObject(String className, String contents) throws Exception {
            super(URI.create("string:///" + className.replace('.', '/')
                    + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
            this.contents = contents;
        }

        public CharSequence getCharContent(boolean ignoreEncodingErrors)
                throws IOException {
            return contents;
        }
    }

    public static void compile(Iterable<? extends JavaFileObject> files) {
        //get system compiler:
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        // for compilation diagnostic message processing on compilation WARNING/ERROR
        EvalTest.MyDiagnosticListener c = new EvalTest.MyDiagnosticListener();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(c,
                Locale.ENGLISH,
                null);
        //specify classes output folder
        Iterable options = Arrays.asList("-d", classOutputFolder);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager,
                c, options, null,
                files);

        Boolean result = task.call();
        if (result == true) {
            System.out.println("Succeeded");
        }
    }

    /*public static void runIt()
     {
     // Create a File object on the root of the directory
     // containing the class file
     File file = new File(classOutputFolder);
 
     try
     {
     // Convert File to a URL
     URL url = file.toURL(); // file:/classes/demo
     URL[] urls = new URL[] { url };
 
     // Create a new class loader with the directory
     ClassLoader loader = new URLClassLoader(urls);
 
     // Load in the class; Class.childclass should be located in
     // the directory file:/class/demo/
     Class thisClass = loader.loadClass("Test");
 
     Class params[] = {};
     Object paramsObj[] = {};
     Object instance = thisClass.newInstance();
     Method thisMethod = thisClass.getDeclaredMethod("test", params);
 
     // run the testAdd() method on the instance:
     thisMethod.invoke(instance, paramsObj);
     }
     catch (MalformedURLException e)
     {
     }
     catch (ClassNotFoundException e)
     {
     }
     catch (Exception ex)
     {
     ex.printStackTrace();
     }
     }*/
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        JavaFileObject file = new EvalTest.InMemoryJavaFileObject("Test", "public class Test"
                + "{"
                + "public void test()"
                + "{"
                + "System.out.println(\"Fancy Pantsy\"); "
                + "}"
                + "public void blub()"
                + "{"
                + "System.out.println(\"Bliblablub\"); "
                + "}"
                + "}");
        Iterable<? extends JavaFileObject> files = Arrays.asList(file);

        //2.Compile your files by JavaCompiler
        compile(files);

        File classfile = new File(classOutputFolder);

        URL url = classfile.toURL(); // file:/classes/demo
        URL[] urls = new URL[]{url};

        // Create a new class loader with the directory
        ClassLoader loader = new URLClassLoader(urls);

        // Load in the class; Class.childclass should be located in
        // the directory file:/class/demo/
        Class thisClass = loader.loadClass("Test");

        Class params[] = {};
        Object paramsObj[] = {};
        Object instance = thisClass.newInstance();
        Method thisMethod = thisClass.getDeclaredMethod("test", params);
        Method anotherMethod = thisClass.getDeclaredMethod("blub", params);

        // run the testAdd() method on the instance:
        thisMethod.invoke(instance, paramsObj);
        anotherMethod.invoke(instance, paramsObj);

        //3.Load your class by URLClassLoader, then instantiate the instance, and call method by reflection
        //runIt();
    }

}
