LOCAL_MATHPAR_PROJECT_DIRECTORY=/home/r1d1/NetBeansProjects
LOCAL_TMP_DIRECTORY_FOR_ARCHIEVE=/home/r1d1
UNIHUB_DIRECTORY_FOR_MATHPAR=/unicluster/home/i.borisov/soft
UNIHUB_IP=172.16.52.9

cd $LOCAL_MATHPAR_PROJECT_DIRECTORY/mathpar/target
zip -u -q -r  $LOCAL_TMP_DIRECTORY_FOR_ARCHIEVE/mathpar.zip classes -x /classes/test_web_parallel\* 
scp $LOCAL_TMP_DIRECTORY_FOR_ARCHIEVE/mathpar.zip i.borisov@$UNIHUB_IP:$UNIHUB_DIRECTORY_FOR_MATHPAR
ssh i.borisov@$UNIHUB_IP  << EOF
pkill java
cd $UNIHUB_DIRECTORY_FOR_MATHPAR
rm -R mathpar_classes
unzip -u -q mathpar.zip
mv classes mathpar_classes
rm mathpar.zip
./start_webcluster_server.sh
EOF


