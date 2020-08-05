 #!/bin/bash

java -cp `dirname $0`/classes/:`dirname $0`/lib/*:/opt/tomcat-latest/lib/* com.symgae.server.cron.CronBatch `dirname $0`
