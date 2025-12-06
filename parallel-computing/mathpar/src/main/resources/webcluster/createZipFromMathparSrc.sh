

LOCAL_MATHPAR_PROJECT_DIRECTORY=/home/r1d1/NetBeansProjects
LOCAL_TMP_DIRECTORY_FOR_ARCHIEVE=/home/r1d1


cd $LOCAL_MATHPAR_PROJECT_DIRECTORY
zip -u -q -r  $LOCAL_TMP_DIRECTORY_FOR_ARCHIEVE/mathpar.zip mathpar -x mathpar/target\* mathpar/.hg\* mathpar/src/main/java/test_web_parallel\*
