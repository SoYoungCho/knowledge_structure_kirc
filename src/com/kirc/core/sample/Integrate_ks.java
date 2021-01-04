package com.kirc.core.sample;

public class Integrate_ks {
    public static void main(String[] args) {
//        System.setProperty("file.encoding","UTF-8");
//        try{
//            Field charset = Charset.class.getDeclaredField("defaultCharset");
//            charset.setAccessible(true);
//            charset.set(null,null);
//        }
//        catch(Exception e){
//            System.out.println("Error");
//        }

        IndexFiles idxf = new IndexFiles();
        idxf.excute();

        try {
            Thread.sleep(5000);
            System.out.println("Thread1 complete");
        } catch (InterruptedException e) {
            System.out.println("Thread1 error");
            e.printStackTrace();
        }

        PreprocNLP unlp = new PreprocNLP();
        unlp.execute();

        try {
            Thread.sleep(5000);
            System.out.println("Thread2 complete");
        } catch (InterruptedException e) {
            System.out.println("Thread2 error");
            e.printStackTrace();
        }

        CreateKS cks = new CreateKS();
        cks.excute();
    }
}
