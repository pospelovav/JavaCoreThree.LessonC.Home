package lesson.c.home;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class Main {
    public static long start;
    private static final int PAGE_SIZE = 1800;  //размер страницы в байтах
    private static byte[] bytes;        //массив для страницы

    public static String readFileWithByte (String fileName){
        String str = null;

        try (FileInputStream in = new FileInputStream(fileName)) {
            byte[] arr = new byte[150];
            int x;
            while ((x = in.read(arr)) > 0) {
                str = new String(arr, 0, x, "UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    private static Enumeration<InputStream> findFilesInDir (String dir) throws FileNotFoundException {
        File[] files = null;
        try {
            File f = new File(dir);
            f.listFiles();
            FileFilter filter = new FileFilter() {
                public boolean accept(File f)
                {
                    return f.getName().endsWith("txt");
                }
            };
            files = f.listFiles(filter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<InputStream> al = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            al.add(new FileInputStream(files[i].getPath()));
        }

        Enumeration<InputStream> e = Collections.enumeration(al);

        return e;
    }

    private static void mergerFile(String dir, String file_out) throws IOException {
        Enumeration<InputStream> e = findFilesInDir(dir);
        FileOutputStream fos = new FileOutputStream(file_out);
        SequenceInputStream in = new SequenceInputStream(e);
        int x;
        while ((x = in.read()) != -1) {
            fos.write(x);
        }
        in.close();
        fos.close();
    }

    private static void pageSelect(RandomAccessFile raf, int position){
        start = System.nanoTime();
        try {
            raf.seek((position - 1) * PAGE_SIZE);
            bytes = new byte[PAGE_SIZE];
            raf.read(bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        try {
//Задание 1. Прочитать файл (около 50 байт) в байтовый массив и вывести этот массив в консоль;
            start = System.nanoTime();
            System.out.println(readFileWithByte("src/main/java/lesson/c/home/1.txt"));
            System.out.println("Time of read and out file: " + (System.nanoTime() - start) / 1000000 + " milliseconds");
            System.out.println();
//Задание 2. Последовательно сшить 5 файлов в один (файлы примерно 100 байт).
            start = System.nanoTime();
            String dir = "src/main/java/lesson/c/home/";  //отсюда берем все файлы txt
            String file_out = "src/main/java/lesson/c/home/mergeFiles/result.txt"; //в этот файл записываем данные файлов из dir
            mergerFile(dir, file_out);    //слияние файлов из dir в file_out
            System.out.println("Time merge files: " + (System.nanoTime() - start) / 1000000 + " milliseconds");

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
/*
Задание 3. Написать консольное приложение, которое умеет постранично читать текстовые файлы (размером > 10 mb).
Вводим страницу (за страницу можно принять 1800 символов), программа выводит ее в консоль.
Контролируем время выполнения: программа не должна загружаться дольше 10 секунд, а чтение – занимать свыше 5 секунд.
*/
        String bookFile = "src/main/java/lesson/c/home/forRead/books.txt";
        try (RandomAccessFile raf = new RandomAccessFile(bookFile, "r")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            long pageCount = raf.length() / 1800 + 1;  //подсчет количества страниц
            int position = 0;
            String str = "";
            System.out.println("Enter page's number for read (Enter Q for exit)");
            while (!str.equals("q")) {
                str = reader.readLine().toLowerCase();
                if (!str.equals("q")){
                    position = Integer.parseInt(str);
                    if ((position > 0) && (position <= pageCount)){
                        pageSelect(raf, position);
                        System.out.println(new String(bytes));
                        System.out.println("------------------------");
                        System.out.println("Page " + position + " of " + pageCount);
                        System.out.println("Time output page: " + (System.nanoTime() - start) / 1000000 + " milliseconds");
                        System.out.println("Enter page's number for read (Enter Q for exit)");
                    } else {
                        System.out.println("Page not found. Try again.");
                        System.out.println("Enter page's number for read (Enter Q for exit)");
                    }
                }
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


