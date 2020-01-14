old="3.0.0"
new="4.0.0-SNAPSHOT"

find ../ -type f \( -name "pom.xml" -o -name "build.gradle*" \) \
  -exec sed -i -e "s/<revision>$old<\/revision>/<revision>$new<\/revision>/g; s/escpos-coffee:$old/escpos-coffee:$new/g" {} \;
