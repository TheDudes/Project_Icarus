package parser;


public class
STTimer
{
        private boolean is_not_running = true;
        private long    time = 0;
        private long    time_dif = 0;

        public void
        set_time(long time)
        {
                if(is_not_running) {
                        this.time = time;
                } else if (time_dif - System.currentTimeMillis() < 0) {
                        is_not_running = true;
                }
        }

        public void
        set_running()
        {
                if(is_not_running) {
                        time_dif = System.currentTimeMillis() + time;
                        is_not_running = false;
                } else if (time_dif - System.currentTimeMillis() < 0) {
                        is_not_running = true;
                }
        }

        public String
        get_state()
        {
                return Boolean.toString(is_not_running);
        }

        public String
        toString()
        {
                return Boolean.toString(is_not_running);
        }
}
