FROM apache/hop:latest

# utilize a url de clonagem do seu repositório

# example value: https://github.com/eliasjebs/treinamento_hop_para_pentaheiros.git

ENV GIT_REPO_URI=https://github.com/Mauriciojebs/Hurbana.git

ENV GIT_REPO_NAME=Hurbana

USER root

RUN apk update  && apk add --no-cache git

# copy custom entrypoint extension shell script

COPY --chown=hop:hop te.docx /home/hop/te.docx

#COPY --chown=hop:hop ./hop_repositorio_hurbana/dev_hurbana-config.json /home/hop/dev_hurbana-config.json

#COPY --chown=hop:hop ./drivers/ /home/hop/drivers/

USER hop