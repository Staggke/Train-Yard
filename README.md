This program simulates a train yard using multiple threads of execution and synchronizing their access to 
shared objects. The trains are represented by the threads and the individual track switches are the shared 
objects.

The train yard consists of 8 different tracks, 4 on each side of the yard, and 5 different switches. 
Trains can arrive on any track on either side of the yard and depart on any track on either side of
the yard for which there is a track/switch configuration allowed by the yard. Only one train on a
given track can move through yard at a time same time regardless of the direction of travel of the
train. There may be multiple trains on the same track moving the same direction waiting to go through
the yard. The track configuration beyond the yard control that allows multiple trains to move on parallel 
tracks. CTC (Centralized Traffic Control) controls trains outside of the train yard.

A train cannot begin moving through the yard until it controls all of the switches that it needs to 
move from its inbound track to its outbound track. Until a train obtains control of all of the switches 
that it needs to move through the yard, it will remain idle on its inbound track. To prevent switch control 
deadlock from occurring, every train must acquire the switches it needs in the order of first switch required, 
second switch required, third switch required.
