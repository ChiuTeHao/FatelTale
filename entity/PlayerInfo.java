package entity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.Vector;
import logger.Logger;
public class PlayerInfo 
{
	private static PlayerInfo uniqueinstance;
	private int totaltype;
	private int health[],attack[];
	private Long attackspeed[],movespeed[];
	private int defense[];;
	private Collider[] _collider;
	private Emitter[] _emitter;
	private String PlayerInfofilePath=System.getProperty("user.dir")+"\\resource\\PlayerInfo.txt";
	private String[] PlayerFilePath;
	private PlayerInfo()
	{
		try
		{
			FileReader fin=new FileReader("./resource/PlayerInfo/PlayerInfo.txt");
			BufferedReader buff=new BufferedReader(fin);
			String str;
			str=buff.readLine();
			totaltype=Integer.parseInt(str);
		    Logger.log(totaltype);
			PlayerFilePath=new String[totaltype];
			health=new int[totaltype];
			attack=new int[totaltype];
			attackspeed=new Long[totaltype];
			defense=new int[totaltype];
			movespeed=new Long[totaltype];
			
			_emitter = new Emitter[totaltype];
			_collider = new Collider[totaltype];
			
			for(int i=0;i<totaltype;i+=1)
				PlayerFilePath[i]=buff.readLine();
			buff.close();
			fin.close();
			for(int i=0;i<totaltype;i+=1)
			{
				fin=new FileReader("./resource/PlayerInfo/"+PlayerFilePath[i]);
				buff=new BufferedReader(fin);
				str=buff.readLine();
				health[i]=Integer.parseInt(str);
				str=buff.readLine();
				attack[i]=Integer.parseInt(str);
				str=buff.readLine();
				attackspeed[i]=Long.parseLong(str);
				str=buff.readLine();
				defense[i]=Integer.parseInt(str);
				str=buff.readLine();
				movespeed[i]=Long.parseLong(str);
				
				_collider[i] = ColliderInfo.getInstance().getCollider(buff);
				_emitter[i] = EmitterInfo.getInstance().getEmitter(buff);
				_emitter[i].setAttacker(i);
				
				fin.close();
				buff.close();
			}
		}
		catch(IOException e)
		{
			Logger.log("Cannot find the file");
		}
		
	}
    @SuppressWarnings(value={"rawtypes", "unchecked"})
	public Vector getTypeInfo(int type)
	{
		Vector v=new Vector(5);
		v.add(health[type]);
		v.add(attack[type]);
		v.add(attackspeed[type]);
		v.add(defense[type]);
		v.add(movespeed[type]);
		return v;
	}
	
	public Emitter getEmitter(int type) {
		assert type >= 0 && type < _emitter.length : "Wrong Index Range.";
		return _emitter[type].clone();
	}
	
	public Collider getCollider(int type) {
		assert type >= 0 && type < _collider.length : "Wrong Index Range.";
		return _collider[type].clone();
	}
	
	public static synchronized PlayerInfo getInstance()
	{
		if(uniqueinstance==null)
			uniqueinstance=new PlayerInfo();
		return uniqueinstance;
	}
	/*public static void main(String[] args)
	{
		PlayerInfo playerinfo;
		playerinfo=PlayerInfo.getInstance();
	}*/
}

