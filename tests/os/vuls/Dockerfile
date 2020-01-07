FROM vuls/vuls as vuls

#Copy vuls test dependencies in the vuls image
WORKDIR /vuls
COPY cve.sqlite3 /vuls/cve.sqlite3
COPY oval.sqlite3 /vuls/oval.sqlite3
COPY config.toml  /vuls/config.toml
COPY mcp.rsa /root/.ssh/id_rsa
COPY config /root/.ssh/config


