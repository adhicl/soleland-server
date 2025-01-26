package sfs2x.extensions.games.SoleLand.Model;

import java.time.Duration;
import java.time.Instant;

public class Mole {
    private boolean isUp;
    private Instant timeStartUp;

    public Mole(){
        isUp = false;
        timeStartUp = Instant.now();
    }

    public void ShowUp(){
        isUp = true;
        timeStartUp = Instant.now();
    }

    public void CheckPutDown(){
        if (!isUp) return;
        
        Instant curTime = Instant.now();
        Duration timeDuration = Duration.between(timeStartUp, curTime);
        if (timeDuration.getSeconds() > 3){
            isUp = false;
        }
    }

    public boolean CheckCanHit(){
        if (!isUp) return false;

        Instant curTime = Instant.now();
        Duration timeDuration = Duration.between(timeStartUp, curTime);
        if (timeDuration.getSeconds() <= 3){
            isUp = false;
            return true;
        }else{
            isUp = false;
        }
        return false;
    }
}
