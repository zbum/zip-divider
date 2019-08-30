package kr.co.manty.zipdivider;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@SpringBootApplication
public class ZipDividerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ZipDividerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        runWithCommonsCompress(args);
    }

    private void runWithJdk(String... args) throws IOException {
        if ( args.length != 2) return;

        String path = args[0].replaceFirst("^~", System.getProperty("user.home"));
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(new File(path).getCanonicalPath()),Charset.forName(args[1]))) {
            ZipEntry entry = null;
            ZipOutputStream zipOutputStream = null;
            for(int inx=1;;inx++) {
                int totalCount = 0;
                zipOutputStream = new ZipOutputStream(new FileOutputStream(inx+".zip"));

                while ( true) {
                    try {
                        entry = zipInputStream.getNextEntry();
                        if (entry == null) break;

                        zipOutputStream.putNextEntry(new ZipEntry(entry.getName()));
                        System.out.println("File :" + entry.getName());
                        totalCount += StreamUtils.copy(zipInputStream, zipOutputStream);
                        zipOutputStream.closeEntry();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if (totalCount> 3.5*1024*1024*1024) {
                        break;
                    }
                }
                zipOutputStream.close();
                if (entry == null) {
                    break;
                }
            }
        }

    }

    private void runWithCommonsCompress(String... args) throws IOException {
        if ( args.length != 2) return;

        String path = args[0].replaceFirst("^~", System.getProperty("user.home"));
        try (ZipArchiveInputStream zipInputStream = new ZipArchiveInputStream(new FileInputStream(new File(path).getCanonicalPath()),args[1])) {
            ZipArchiveEntry entry = null;
            ZipArchiveOutputStream zipOutputStream = null;
            for(int inx=1;;inx++) {
                int totalCount = 0;
                zipOutputStream = new ZipArchiveOutputStream(new FileOutputStream(inx+".zip"));

                while ( true) {
                    try {
                        entry = zipInputStream.getNextZipEntry();
                        if (entry == null) break;

                        zipOutputStream.putArchiveEntry(new ZipArchiveEntry(entry.getName()));
                        System.out.println("File :" + entry.getName());
                        totalCount += StreamUtils.copy(zipInputStream, zipOutputStream);
                        zipOutputStream.closeArchiveEntry();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if (totalCount> 3.5*1024*1024*1024) {
                        break;
                    }
                }
                zipOutputStream.close();
                if (entry == null) {
                    break;
                }
            }
        }

    }
}
