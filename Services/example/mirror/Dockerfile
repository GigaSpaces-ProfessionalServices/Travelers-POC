FROM gigaspaces/smart-cache-enterprise:17.1.1
COPY target/*.jar /opt/gigaspaces/bin/pu.jar 

ENTRYPOINT ["/opt/entrypoint.sh"]
CMD ["host", "run-agent", "--auto"]
