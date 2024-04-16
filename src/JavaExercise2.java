import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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
                if(cache.containsKey(x)) return cache.get(x);
            }finally {
                lock.readLock().unlock();
            }

            boolean outPut = computeIfIsPrime(x);
            lock.writeLock().lock();
            try{
                cache.put(x, outPut);
            }finally {
                lock.writeLock().unlock();
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

        




    }
}
