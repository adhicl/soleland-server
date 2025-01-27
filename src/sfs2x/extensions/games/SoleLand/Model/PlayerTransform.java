package sfs2x.extensions.games.SoleLand.Model;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

public class PlayerTransform {
    private double x;	  
    private double y;	  
    private double z;	  
    private double rotx;	  
    private double roty;	  
    private double rotz;
    private long timeStamp = 0L; 
	  
    public static PlayerTransform GetSpawnPosition(int index) {
        PlayerTransform[] spawnPoints = getSpawnPoints();        
        return spawnPoints[index];
    }
    // Hard coded spawnPoints - where players will spawn
    private static PlayerTransform[] getSpawnPoints() {
        PlayerTransform[] spawnPoints = new PlayerTransform[2];
        spawnPoints[0] = new PlayerTransform(-3.0D, 2.2D, -7D, 0.0D, 0.0D, 0.0D);
        spawnPoints[1] = new PlayerTransform(3.0D, 2.2D, -7D, 0.0D, 0.0D, 0.0D);
        return spawnPoints;
    }

    public PlayerTransform(double x, double y, double z, double rotx, double roty, double rotz) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotx = rotx;
        this.roty = roty;
        this.rotz = rotz;
    }
	  
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
    
    public long getTimeStamp() {
        return this.timeStamp;
    }
	  
    public static PlayerTransform fromSFSObject(ISFSObject data) {
        ISFSObject transformData = data.getSFSObject("transform");
        double x = transformData.getDouble("x").doubleValue();
        double y = transformData.getDouble("y").doubleValue();
        double z = transformData.getDouble("z").doubleValue();
        double rx = transformData.getDouble("rx").doubleValue();
        double ry = transformData.getDouble("ry").doubleValue();
        double rz = transformData.getDouble("rz").doubleValue();
        long timeStamp = transformData.getLong("t").longValue();
        PlayerTransform transform = new PlayerTransform(x, y, z, rx, ry, rz);
        transform.setTimeStamp(timeStamp);
        return transform;
    }
	  
    public void toSFSObject(ISFSObject data) {
        SFSObject sFSObject = new SFSObject();
        sFSObject.putDouble("x", this.x);
        sFSObject.putDouble("y", this.y);
        sFSObject.putDouble("z", this.z);
        sFSObject.putDouble("rx", this.rotx);
        sFSObject.putDouble("ry", this.roty);
        sFSObject.putDouble("rz", this.rotz);
        sFSObject.putLong("t", this.timeStamp);
        data.putSFSObject("transform", (ISFSObject)sFSObject);
    }
    
}
