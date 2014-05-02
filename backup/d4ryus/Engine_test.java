import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

class Engine_test {

    static public ScriptEngineManager factory = new ScriptEngineManager();
    static public ScriptEngine engine = factory.getEngineByName("JavaScript");

    public static void main(String[] args) throws Exception {

        double avg = 0.0;

        for(int i = 0; i < 1000; i++) {

            String test0 = "true == false != true && false";
            String test1 = "true";
            String test2 = "false";
            String test3 = (Math.random() * 100) + " + 7";
            String test4 = (Math.random() * 100) + " + 1";
            String test5 = (Math.random() * 100) + " + 901";
            String test6 = (Math.random() * 100) + " > 5";
            String test7 = (Math.random() * 100) + " < 123.122134";
            String test8 = (Math.random() * 100) + " <= 10987123";
            String test9 = (Math.random() * 100) + " < 18723 >= " + (Math.random() * 100);

            long now = System.currentTimeMillis();

            boolean result0 = (boolean)engine.eval(test0);
            boolean result1 = (boolean)engine.eval(test1);
            boolean result2 = (boolean)engine.eval(test2);
            double  result3 = (double)engine.eval(test3);
            double  result4 = (double)engine.eval(test4);
            double  result5 = (double)engine.eval(test5);
            boolean result6 = (boolean)engine.eval(test6);
            boolean result7 = (boolean)engine.eval(test7);
            boolean result8 = (boolean)engine.eval(test8);
            boolean result9 = (boolean)engine.eval(test9);

            now = System.currentTimeMillis() - now;

            avg += now;
            if(i % 100 == 0 && i != 0) {
                avg = avg / 100;
                try {
                    System.out.print("Sleep 1 sec (" + avg + "ms avg)");
                    //Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            if(i % 10 == 0 && i != 0)
                System.out.println();

            System.out.print(now + "ms\t");
        }
        System.out.println("end (" + avg / 100 + "ms avg)");
    }
}
