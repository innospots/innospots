From innospots-base:latest

ENV APPLICATION_NAME innospots-isp-server

ENV SERVER_MAIN_CLASS io.innospots.workflow.runtime.server.InnospotsWorkflowServer

ADD target/$APPLICATION_NAME.tar.gz /innospot/

ENTRYPOINT ["sh","/innospot/bin/app_entrypoint.sh"]