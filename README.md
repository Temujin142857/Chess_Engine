# Chess_Engine

##steps:
1. Create a usable and compact dataset
   a. go line by line in streamer, reading and immediately writing
2. Set up the machine learning

don't think it does the thing below, keeping it for now in case it shows up again
it also tries to attack the queen with the bishop even though the queen can capture it
so it might not be seeing queen captures? compare queen class to bishop class again

next step, help it react properly to check, which I think is still broken

sigh, checkmate detection is broken again, fixed by updating possible moves to update when blocked moves.conatins(location2)
checkmate broke again, only when two engines play against each other though