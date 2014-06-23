package parser;


public class
STTimer
{
        private boolean is_not_running = true;
        private long    time = 0;
        private long    time_dif = 0;

        /**
         * set_time can set the time for the time, how long the timer should run.
         * it will also update the time if called again on a running timer
         * @param time long value of the time to set
         */
        public void
        set_time(long time)
        {
                if(is_not_running) {
                        this.time = time;
                } else if (time_dif - System.currentTimeMillis() < 0) {
                        is_not_running = true;
                }
        }

        /**
         * set_running will set the time running and will also update the time if called
         * again on a running timer
         */
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

        /**
         * get_state will return the boolean is_not_running as String
         * @return the String representation of the boolean is_not_running
         */
        public String
        get_state()
        {
                return Boolean.toString(is_not_running);
        }

        /**
         * toString is more or less the same like get_state
         */
        public String
        toString()
        {
                return get_state();
        }
}
