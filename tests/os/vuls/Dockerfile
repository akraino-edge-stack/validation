FROM vuls/vuls as vuls

#Copy vuls test dependencies in the vuls image
WORKDIR /home/pharos/dlst/vuls/validation/tests/os/vuls
COPY cve.sqlite3 /vuls/cve.sqlite3
COPY oval.sqlite3 /vuls/oval.sqlite3
COPY config.toml  /vuls/config.toml
COPY environment.sh /vuls/environment.sh

