Repackaging of the latest versions of AIDAs code. 

HOW TO "CURRENTLY" RUN AIDA, CAN EASILY BE AUTOMATED WITH DOCKERFILE

Turn on AIDA

Wait 2-3 minutes

Connect laptop to AIDA network ( pw: aidaproject )

Start app on pad and make sure network is connected there aswell

SSH into AIDA through terminal ( " ssh aida@192.168.50.195 " ) ( pw: aida9319 )

Run the following commands in terminal

" sudo docker build --no-cache -t ros2-aida . "         ( First time only, after this you can skip --no-cache, will run for a few minutes )

" sudo docker rm ros2-aida-dev "

" sudo docker run -it --privileged --network host --device=/dev/ttyACM0 --name ros2-aida-dev ros2-aida bash "

( If building without --no-cache, make sure you git pull the latest code before next step )

" colcon build "

" source install/setup.bash "

" ros2 launch aida_api all.yaml "

Connect through the app on the pad.

( CTRL + C to safely shut down AIDAs nodes )
