package redis;

import java.util.concurrent.ThreadLocalRandom;

public class HyperLogLogTest {
	// 静态内部类
	// 通过随机数记录最大的低位零的个数
	static class BitKeeper {
		private int maxbits;

		public void random(long value) {
//			long value = ThreadLocalRandom.current().nextLong(2L << 32);
			int bits = lowZeros(value);
			if (bits > this.maxbits) {
				this.maxbits = bits;
			}
		}

		// 算低位零的个数
		private int lowZeros(long value) {
			int i = 1;
			for (; i < 32; i++) {
				if (value >> i << i != value) {
					break;
				}
			}
			return i - 1;
		}
	}

	static class Experiment {
		private int n;
		private int k;
		private BitKeeper[] bitKeepers;

		public Experiment(int n) {
			this(n,1024);
		}
		
		

		public Experiment(int n, int k) {
			this.n = n;
			this.k = k;
			this.bitKeepers=new BitKeeper[k];
			for(int i=0;i<k;i++) {
				this.bitKeepers[i]=new BitKeeper();
			}
		}



		public void work() {
			for (int i = 0; i < this.n; i++) {
				long m = ThreadLocalRandom.current().nextLong(1L<<32);
				BitKeeper keeper=bitKeepers[(int)(((m&0xfff000)>>16)%bitKeepers.length)];
				keeper.random(m);
			}
		}

		public double estimate() {
			double sumbitsInverse=0.0;
			for (BitKeeper bitKeeper : bitKeepers) {
				sumbitsInverse+=1.0/(float)bitKeeper.maxbits;
			}
			double avgBits=(float)bitKeepers.length/sumbitsInverse;
			return Math.pow(2, avgBits)*this.k;
		}
	}

	public static void main(String[] args) {
		for (int i = 100000; i < 1000000; i += 100000) {
			Experiment exp = new Experiment(i);
			exp.work();
			double est = exp.estimate();
			System.out.printf("%d %.2f %.2f\n",i,est,Math.abs(est-i)/i);
		}
	}

}
