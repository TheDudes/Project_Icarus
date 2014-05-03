#!/usr/bin/env zsh

if [[ `ps | grep $$ | awk '{ print $4 }'` != zsh ]]
then
    echo "Error: zsh needed."
    echo "Error: Please use \"zsh java-switcher.sh\" or \"./java-switcher.sh\""
    exit 1
fi

BUILD=./build.xml
ANT=/usr/bin/ant
README=./README
CAT=/usr/bin/cat

if [[ ! (-s ${README} && -r ${README}) ]];
then
    print "Only basic help availible."
fi

if [[ ! (-s ${BUILD} && -r ${BUILD}) ]];
then
    print "can't find ${BUILD}!"
    print "but i need it to work."
    print "exiting ..."
    exit 1
fi

if [[ ! (-s ${ANT} && -r ${ANT}) ]];
then
    print "can't find ${ANT}!"
    print "please install/reinstall apache-ant."
    print "exiting ..."
    exit 1
fi

case $1 in
    compile|compile:)
	$ANT -f $BUILD compile
	;;
    javadoc|javadoc:)
	$ANT -f $BUILD javadoc
	;;
    clean|clean:)
	$ANT -f $BUILD clean
	;;
    jar|jar:)
	$ANT -f $BUILD jar
	;;
    clean-build|clean-build:)
	$ANT -f $BUILD clean-build
	;;
    run|run:)
	$ANT -f $BUILD run
	;;
    magic|magic:)
	$ANT -f $BUILD magic
	;;
    *)
	print "usage: ./make.sh [clean|compile|javadoc|jar|clean-build|run|magic]"
	print " "
	print "All these tags are also ant targets, that means that you can also run:"
	print " "
	print "\tant -f build.xml TAG"
	print "\t OR"
	print "\tant TAG"
	print "\t OR"
	print "\tant"
	print " "
	print "ant without parameters will default to \"magic\""
	print " "
	print "Tags:"
	print "\tclean:\t\tclean up the build directory"
	print "\tcompile:\tcompiles the changes files"
	print "\tjavadoc:\tgenerates javadoc"
	print "\tjar:\t\tgenerates a jar archive of the classes"
	print "\tclean-build:\truns all four actions from abouve"
	print "\trun:\t\tonly runs the jar file"
	print "\tmagic:\t\tdoes everything"
	print " "
	;;
esac

exit 0
