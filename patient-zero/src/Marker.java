import java.util.*;

public class Marker {

    public static int[] prefixFunction(String p) {
        int m = p.length();                            // длина маркера
        int[] pi = new int[m];                         // массив ответов, pi[0] = 0 всегда (у одной буквы нет собственной границы)
        int k = 0;                                     // k = длина текущей границы (сколько букв префикса уже совпало)
        for (int i = 1; i < m; i++) {                  // идём по маркеру слева направо, начиная со второй буквы
            while (k > 0 && p.charAt(i) != p.charAt(k))// пока следующая буква не подходит к текущей границе...
                k = pi[k - 1];                         // ...уменьшаем границу до следующей более короткой (идём по цепочке границ)
            if (p.charAt(i) == p.charAt(k)) k++;       // буква подошла: граница стала на 1 длиннее
            pi[i] = k;                                 // записываем длину границы для позиции i
        }
        return pi;
    }


    public static int[] findAll(String text, String p) {
        int m = p.length();                            // длина маркера
        if (m == 0) return new int[0];                 // пустой маркер: искать нечего
        int[] pi = prefixFunction(p);                  // таблица границ маркера
        List<Integer> res = new ArrayList<>();         // сюда складываем найденные позиции
        int k = 0;                                     // k = сколько букв маркера сейчас совпало с текстом
        for (int i = 0; i < text.length(); i++) {      // i - указатель по тексту, только вперёд
            while (k > 0 && text.charAt(i) != p.charAt(k)) // несовпадение при уже совпавших k буквах:
                k = pi[k - 1];                         // откат по таблице (не начинаем заново с 0, а используем границу)
            if (text.charAt(i) == p.charAt(k)) k++;    // буква совпала: совпавшая часть выросла на 1
            if (k == m) {                              // совпал весь маркер целиком
                res.add(i - m + 1);                    // начало вхождения = текущая позиция - длина + 1
                k = pi[k - 1];                         // не обнуляем k: следующее вхождение может пересекаться с этим
            }
        }
        int[] out = new int[res.size()];               // переводим список в массив int[]
        for (int i = 0; i < out.length; i++) out[i] = res.get(i);
        return out;
    }

    private static final long MOD = 1_000_000_007L;    // большое простое число: хеш берём по модулю, чтобы он не переполнялся
    private static final long BASE = 257L;             // основание хеша (больше размера алфавита)


    public static int sharedWords(String a, String b, int k) {
        int n = a.length(), m = b.length();            // длины строк
        if (k <= 0 || n < k || m < k) return 0;        // окон длины k нет - ответ 0
        long high = 1;                                 // high = BASE^(k-1) mod MOD: вес самой старой (левой) буквы окна
        for (int i = 0; i < k - 1; i++) high = high * BASE % MOD;

        // Шаг 1: считаем хеши всех окон b и запоминаем, с каких позиций они начинаются
        Map<Long, List<Integer>> windowsOfB = new HashMap<>(); // хеш -> список начал окон b с таким хешем
        long h = 0;                                    // хеш текущего окна
        for (int i = 0; i < k; i++) h = (h * BASE + b.charAt(i)) % MOD; // хеш первого окна считаем "в лоб" (схема Горнера)
        windowsOfB.computeIfAbsent(h, x -> new ArrayList<>()).add(0);   // кладём первое окно
        for (int i = k; i < m; i++) {                  // двигаем окно на 1 букву вправо
            // O(1): убираем старую букву (b[i-k]*high), умножаем на BASE (сдвиг), добавляем новую b[i]
            // "+ MOD" перед % нужен, чтобы результат вычитания не стал отрицательным
            h = ((h - b.charAt(i - k) * high % MOD + MOD) % MOD * BASE + b.charAt(i)) % MOD;
            windowsOfB.computeIfAbsent(h, x -> new ArrayList<>()).add(i - k + 1); // запоминаем начало окна
        }

        // Шаг 2: идём окном по a и ищем каждый хеш среди хешей b
        int count = 0;                                 // счётчик позиций
        h = 0;
        for (int i = 0; i < k; i++) h = (h * BASE + a.charAt(i)) % MOD; // хеш первого окна a
        for (int i = 0; ; i++) {                       // окно a[i..i+k-1]
            List<Integer> cand = windowsOfB.get(h);    // кандидаты в b с таким же хешем
            if (cand != null) {
                for (int s : cand) {                   // равные хеши НЕ доказывают равенство слов (коллизии), поэтому...
                    boolean same = true;
                    for (int t = 0; t < k; t++)        // ...сравниваем буквы по одной
                        if (a.charAt(i + t) != b.charAt(s + t)) { same = false; break; }
                    if (same) { count++; break; }      // нашли настоящее совпадение: позиция i засчитана один раз
                }
            }
            if (i + k >= n) break;                     // окно дошло до конца a - выходим
            // сдвигаем окно a на 1 вправо тем же приёмом за O(1)
            h = ((h - a.charAt(i) * high % MOD + MOD) % MOD * BASE + a.charAt(i + k)) % MOD;
        }
        return count;
    }
}
