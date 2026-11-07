import java.util.*;

/**
 * Миссия 5. Динамическое программирование на дереве заражений. Дерево подвешиваем за человека 0 (корень).
 */

public class Outbreak {
    public static boolean[] cheapestTests(int n, int[][] nb, int[] cost) {
        boolean[] tested = new boolean[n];             // (запасной результат для n == 0)
        if (n == 0) return tested;
        int[] parent = new int[n];                     // parent[v] = от кого мы пришли в v (в файле направление связей не дано)
        int[] order = bfsOrder(n, nb, parent);         // порядок обхода от корня: родитель всегда раньше детей
        long[] in = new long[n], out = new long[n];    // два состояния ДП
        for (int idx = n - 1; idx >= 0; idx--) {       // идём с конца порядка: сначала листья, потом родители
            int v = order[idx];
            in[v] = cost[v];                           // v тестируем: платим за него
            out[v] = 0;                                // v не тестируем: пока 0
            for (int c : nb[v]) {                      // перебираем соседей v
                if (c == parent[v]) continue;          // родителя пропускаем (он не ребёнок)
                in[v] += Math.min(in[c], out[c]);      // v тестируем -> ребёнок свободен: берём дешевле из двух вариантов
                out[v] += in[c];                       // v не тестируем -> ребёнок ОБЯЗАН быть протестирован
            }
        }
        // Восстановление ответа: спускаемся от корня и повторяем то же правило, чтобы узнать, КТО тестируется
        boolean[] take = new boolean[n];               // take[v] = true, если v тестируем
        take[0] = in[0] <= out[0];                     // у корня выбираем меньшее из двух чисел
        for (int idx = 0; idx < n; idx++) {            // от корня вниз (родитель раньше детей)
            int v = order[idx];
            for (int c : nb[v]) {
                if (c == parent[v]) continue;          // только дети
                if (!take[v]) take[c] = true;          // родитель не тестируется -> ребёнок обязан
                else take[c] = in[c] <= out[c];        // родитель тестируется -> ребёнок по более дешёвому варианту
            }
        }
        return take;
    }


    public static int longestChain(int n, int[][] nb) {
        if (n <= 1) return 0;                          // один человек - путей нет
        int[] parent = new int[n];
        int[] order = bfsOrder(n, nb, parent);         // порядок от корня
        int[] down = new int[n];                       // down[v] = самый длинный путь вниз от v (в рёбрах)
        int best = 0;                                  // лучший найденный путь
        for (int idx = n - 1; idx >= 0; idx--) {       // от листьев к корню
            int v = order[idx];
            int first = 0, second = 0;                 // две самые длинные ветки вниз от v
            for (int c : nb[v]) {
                if (c == parent[v]) continue;          // только дети
                int len = down[c] + 1;                 // длина ветки через ребёнка c: его путь вниз + ребро v-c
                if (len > first) { second = first; first = len; } // новая самая длинная: старая становится второй
                else if (len > second) second = len;  // иначе, возможно, вторая по длине
            }
            down[v] = first;                           // от v вниз идём по самой длинной ветке
            best = Math.max(best, first + second);     // путь, который поднимается из одной ветки и спускается в другую
        }
        return best;
    }

    private static int[] bfsOrder(int n, int[][] nb, int[] parent) {
        int[] order = new int[n];                      // очередь = одновременно итоговый порядок
        boolean[] seen = new boolean[n];               // посещён ли человек (чтобы не ходить по кругу)
        int head = 0, tail = 0;                        // head - откуда читаем, tail - куда пишем
        order[tail++] = 0; seen[0] = true; parent[0] = -1; // стартуем с корня
        while (head < tail) {                          // пока очередь не пуста
            int v = order[head++];                     // берём следующего человека
            for (int c : nb[v]) {                      // все его соседи
                if (!seen[c]) { seen[c] = true; parent[c] = v; order[tail++] = c; } // новый сосед = ребёнок v
            }
        }
        return order;
    }
}
