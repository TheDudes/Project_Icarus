--README from Icarus interpreter--

to build/run/... apache-ant is needed.

you have to be inside the Icarus folder, where the 'build.xml' (and this
README.txt) is located. do not forget to configure ur OWN! (which is NOT under
version control) config file, and specify it inside the Main.java file 
(/Icarus/src/Icarus/Main.java).

Usage:
    ant [compile|javadoc|clean|clean-build|run|all|magic]

compile     -> will compile
javadoc     -> creates javadoc
clean       -> deletes .class files
clean-build -> clean, compile, javadoc and create jar
run         -> run
all         -> clean, compile, javadoc, create jar and run
magic       -> clean, compile, create jar and run

info:
        Icarus wont do much without a running IO server, so we made a simple
        Echo server (located at: build/classes/IOInterface/testServer.java)
        to start it just enter:
        'ant compile && cd build/classes && java IOInterface.testServer'
