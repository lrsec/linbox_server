#java -cp ".:../lib/*:../config:"  -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Xloggc:tcpServer.gc.log -verbose:gc -XX:-UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=10M -XX:-PrintGCDetails -XX:HeapDumpPath=./tcp_server.hprof -XX:-HeapDumpOnOutOfMemoryError -Xms6g -Xmx10g  com.medtree.im.server.connector.tcp.ImTcpServer
java -cp ".:../lib/*:../config:" -Xloggc:tcpServer.gc.log -verbose:gc -XX:-UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=10M -XX:-PrintGCDetails -XX:HeapDumpPath=./tcp_server.hprof -XX:-HeapDumpOnOutOfMemoryError -Xms6g -Xmx10g  com.medtree.im.server.connector.tcp.ImTcpServer