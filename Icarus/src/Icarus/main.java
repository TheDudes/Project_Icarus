package Icarus;

import parser.*;
import Ninti.*;
import linc.*;

public class main{
    public static void main(String[] args) throws Exception{
        String[] path = {"/home/d4ryus/coding/Project_Icarus/franzke_files/plc_prg.st"};
        //InfoCollector infR= new InfoCollector(path);
        System.out.println(MergeFiles.mergeAll(path));
    }
}
