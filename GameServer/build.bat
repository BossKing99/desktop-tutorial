xcopy bin\com\ Builddata\com /S
cd .\Builddata\ 
jar -cvfm ../Server.jar MANIFEST.MF ./
cd ..
docker build -t="weberkuo/bp-system" .
