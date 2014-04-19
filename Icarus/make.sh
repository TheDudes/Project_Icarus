#!/usr/bin/zsh

if [[ `ps | grep $$ | awk '{ print $4 }'` != zsh ]]
then
    echo "Error: zsh needed."
    echo "Error: Please use \"zsh java-switcher.sh\" or \"./java-switcher.sh\""
    exit 1
fi

BUILD=./build.xml
ANT=/usr/bin/ant

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

$ANT -f $BUILD
