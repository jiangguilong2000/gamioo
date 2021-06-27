FROM ccr.ccs.tencentyun.com/jiangguilong/kona-jdk8:1.0
MAINTAINER jiangguilong jiangguilong@gamioo.io
RUN mkdir /data/server
ADD nginx.tar.gz /data/server
RUN yum install  lrzsz -y
RUN yum install vim -y

ENTRYPOINT ["/bin/bash", "start.sh"]

