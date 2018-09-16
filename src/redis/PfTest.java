package redis;

import redis.clients.jedis.Jedis;

public class PfTest {

	public static void main(String[] args) {
		Jedis jedis=new Jedis();
		for(int i=0;i<1000;i++) {
			jedis.pfadd("num2", "user"+i);
			long total=jedis.pfcount("num2");
			if(total!=i+1) {  //当jedis统计的数量和计数不等时
				System.out.printf("%d %d\n",total,i+1);
				break;
			}
		}
		jedis.close();
	}
}
