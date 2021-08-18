package me.GK.core.managers;

import me.GK.core.GKCore;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class CloudNetFileManager {
    public static void copyFolder(Path source, Path target, CopyOption... options) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("creating folder: " + dir.toAbsolutePath());
                Files.createDirectories(target.resolve(source.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("copying file: " + file.toAbsolutePath());
                Files.copy(file, target.resolve(source.relativize(file)), options);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * to copy folder from template folder to current server
     * @param templatePath example: Center/minigame_system, GKPM/default
     */
    public static void applyTemplate(String templatePath){
        String fromPath = "../../../local/templates/"+templatePath;
        String toPath = "./";
        Path from = Paths.get(fromPath);
        Path to = Paths.get(toPath);
        try {
            copyFolder(from, to, StandardCopyOption.REPLACE_EXISTING);
            Bukkit.broadcastMessage(GKCore.instance.messageSystem.get("successfullyAppliedTemplate"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Bukkit.broadcastMessage(GKCore.instance.messageSystem.get("failedAppliedTemplate"));
        }
    }
}
