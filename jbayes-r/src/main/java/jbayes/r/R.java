/*
 *
 * Copyright (C) 2015 Dmytro Grygorenko <dmitrygrig@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jbayes.r;

import jbayes.util.Ensure;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dmytro Grygorenko <dmitrygrig(at)gmail.com>
 */
public class R {

    private static final Logger LOGGER = LoggerFactory.getLogger(R.class);

    private Rengine re;
    private final String homeDir;

    // ******** singleton ********
    private static R instance;

    public static R getInstance(String rHomeDir) {
        Ensure.NotNullOrEmpty(rHomeDir, "R Home Directory");
        if (instance == null) {
            instance = new R(rHomeDir);
            if (!instance.init()) {
                throw new IllegalStateException("R cannot be initialized.");
            }
        }
        return instance;
    }

    public static R getInstance() {
        if (instance == null) {
            String rHomeDir = System.getenv("R_HOME");
            instance = getInstance(rHomeDir);
        }
        return instance;
    }

    public R(String homeDir) {
        this.homeDir = homeDir;
    }

    public boolean init() {
        return init(new String[0]);
    }

    public boolean init(String[] args) {
        // just making sure we have the right version of everything
        if (!Rengine.versionCheck()) {
            LOGGER.warn("** Version mismatch - Java files don't match library version.");
            System.exit(1);
        }
        LOGGER.info("Creating Rengine (with arguments)");
        // 1) we pass the arguments from the command line
        // 2) we won't use the main loop at first, we'll start it later
        //    (that's the "false" as second argument)
        // 3) the callbacks are implemented by the TextConsole class above
        re = new Rengine(args, false, new TextConsole());
        LOGGER.info("Rengine created, waiting for R");
        // the engine creates SingleR is a new thread, so we should wait until it's ready
        if (!re.waitForR()) {
            LOGGER.warn("Cannot load R");
            return false;
        }

        return true;
    }

    public REXP eval(String cmd) {
        checkRNotNull();
        Ensure.NotNullOrEmpty(cmd, "R Command");
        
        REXP res = re.eval(cmd);
        LOGGER.trace(cmd);
        return res;
    }

    public Rengine getRe() {
        return re;
    }

    public void lib(String name) {
        Ensure.NotNullOrEmpty(name, "R Library");
        eval(String.format("library(%s, lib.loc=\"%s\")", name, homeDir));
    }

    public String libPaths() {
        return eval(".libPaths()").asString();
    }

    public void clearAll() {
        eval("rm(list=ls())");
    }
    
      protected void checkRNotNull(){
        Ensure.NotNull(re, "R Engine is not yet initialized");
    }
}
