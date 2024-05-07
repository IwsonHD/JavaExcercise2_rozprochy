import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JavaExercise2 {

    private static class CachingPrimeChecker {
        // TODO: dokończ implementację cache'u
        // (ustal typ przechowywanej wartości oraz rodzaj wykorzystywanej mapy)
        private final Map<Long, Boolean> cache = new HashMap<>();
        private final ReadWriteLock lock =  new ReentrantReadWriteLock();
        public boolean isPrime(final long x) {
            // TODO: dokończ implementację sprawdzania czy liczba x jest liczbą pierwszą
            // Należy zagwarantować, że dla każdej unikalnej liczby obliczenia zostaną wykonane tylko 1 raz
            // Ponowne (w tym równoległe) sprawdzenie czy dana liczba jest liczbą pierwszą
            // powinny wykorzystać cache
            lock.readLock().lock();
            try{
                if(cache.containsKey(x)){
                    System.out.println(x + " is prime: " + cache.get(x));
                    return cache.get(x);
                }
            }finally {
                lock.readLock().unlock();
            }

            boolean outPut;
            lock.writeLock().lock();
            outPut = computeIfIsPrime(x);
            try{
                cache.put(x, outPut);
            }finally {
                lock.writeLock().unlock();
                System.out.println(x + " is prime: " + outPut);
                return outPut;
            }

        }

        // Funkcja sprawdzająca czy dana liczba jest liczbą pierwszą, należy jej użyć do
        // wykonywania obliczeń
        // Nie należy jej w żaden sposób modyfikować!
        private boolean computeIfIsPrime(long x) {
            final String currentThreadName = Thread.currentThread().getName();
            System.out.printf("\t[%s] Running computation for: %d%n", currentThreadName, x);
            try {
                // symulacja długich obliczeń
                Thread.sleep(TimeUnit.SECONDS.toMillis(10));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (x < 2) {
                return false;
            }
            for (long i = 2; i * i <= x; i++) {
                if (x % i == 0) {
                    return false;
                }
            }
            return true;
        }
    }

    public static void main(String[] args) {
        // TODO: zaimplementuj pętlę główną programu
        final CyclicBarrier endCyclicBarrier = new CyclicBarrier(4);
        final CyclicBarrier startCyclicBarrier = new CyclicBarrier(4);
        final ExecutorService executorService = Executors.newFixedThreadPool(4);
        Scanner scanner = new Scanner(System.in);
        CachingPrimeChecker cachingPrimeChecker = new CachingPrimeChecker();
        do{
            String input = scanner.nextLine();

            String[] numsAsString = input.split(" ");
            if (numsAsString.length < 4) {
                System.out.println("Wprowadź 4 liczby całkowite oddzielone spacjami.");
                continue;
            }
            int[] numsToCalc = new int[4];
            try {

                for (int i = 0; i < 4; i++) {
                    numsToCalc[i] = Integer.parseInt(numsAsString[i]);
                }
                for(int i = 0; i < 4; i++){
                    final int num = numsToCalc[i];
                    executorService.submit(()->{
                        try {
                            startCyclicBarrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            throw new RuntimeException(e);
                        }
                        cachingPrimeChecker.isPrime(num);
                        try {
                            endCyclicBarrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }

            } catch (NumberFormatException e) {

                System.out.println("Wprowadź tylko liczby całkowite oddzielone spacjami.");
            }



        }while(true);
    }
}
