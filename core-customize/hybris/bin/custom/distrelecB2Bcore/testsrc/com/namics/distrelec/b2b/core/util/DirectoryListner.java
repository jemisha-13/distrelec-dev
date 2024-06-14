/*
 * Copyright 2000-2016 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.util;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Example to watch a directory (or tree) for changes to files. Start Jvm with following instruction -Dout.dir=output directory space source
 * files directory to scan incoming file space filename pattern [a-zA-Z_0-9]*.zip
 */

public class DirectoryListner {

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private final boolean recursive;
    private boolean trace = false;

    private static final Logger LOG = Logger.getLogger(DirectoryListner.class);

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                LOG.info(String.format("register: %s\n", dir));
            } else {
                if (!dir.equals(prev)) {
                    LOG.info(String.format("update: %s -> %s\n", prev, dir));
                }
            }
        }
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Creates a WatchService and registers the given directory
     */
    DirectoryListner(Path dir, boolean recursive) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
        this.recursive = recursive;

        if (recursive) {
            LOG.info(String.format("Scanning %s ...\n", dir));
            registerAll(dir);
            System.out.println("Done.");
        } else {
            register(dir);
        }

        // enable trace after initial registration
        this.trace = true;
    }

    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents(final String baseDirpath, final String filenamepattern) {
        for (;;) {
            LOG.info("Waiting for new incoming file !!");
            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                LOG.error("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                // print out event
                LOG.info(String.format("%s: %s\n", event.kind().name(), child));

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (kind == ENTRY_CREATE) {
                    try {
                        // trigger event only when matching file has been created
                        if (child.getFileName().toString().matches(filenamepattern)) {
                            if (Files.isDirectory(child, NOFOLLOW_LINKS) && recursive) {
                                registerAll(child);
                            }
                            final Filemerge filemerge = new Filemerge(baseDirpath, filenamepattern);
                            filemerge.perform();
                        }
                    } catch (Exception x) {
                        LOG.error("Error occured while processing file\n" + x.getMessage());
                    }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    static void usage() {
        LOG.error("usage: java -Dout.dir=/blah/out  DirectoryListner /blah/SourceDir filenamepattern ");
        System.exit(-1);
    }

    public static void main(String[] args) throws IOException {
        LOG.info("Started Directory Listner Service...");
        // parse arguments
        if (args.length == 0 && args.length > 2)
            usage();

        LOG.info("Listnering to dir ..." + args[0]);
        LOG.info("Listnering for filepattern ..." + args[1]);

        boolean recursive = false;
        // register directory and process its events
        Path dir = Paths.get(args[0]);
        new DirectoryListner(dir, recursive).processEvents(args[0], args[1]);
    }
}
