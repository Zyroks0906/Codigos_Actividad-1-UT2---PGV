import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Escenario 2: Cola de Impresión CON orden estricto
 * Las solicitudes se atienden en el orden exacto en que fueron realizadas
 * 
 * @author AlejandroMejiasRamirez
 */
public class ImpresorasConOrden {
    
    // Semáforos para controlar el acceso a las impresoras
    private static Semaphore impresorasNegro = new Semaphore(3, true); // Fair = true (FIFO)
    private static Semaphore impresorasColor = new Semaphore(2, true);  // Fair = true (FIFO)
    
    // Locks para garantizar orden estricto por tipo
    private static Lock lockNegro = new ReentrantLock(true); // Fair lock
    private static Lock lockColor = new ReentrantLock(true); // Fair lock
    
    // Contador de orden de llegada
    private static int contadorOrden = 0;
    private static Lock lockContador = new ReentrantLock();
    
    /**
     * Clase que representa un usuario que necesita imprimir
     */
    static class Usuario extends Thread {
        private String nombre;
        private boolean esColor;
        private int ordenLlegada;
        
        public Usuario(String nombre, boolean esColor) {
            this.nombre = nombre;
            this.esColor = esColor;
            
            // Asignar orden de llegada de forma thread-safe
            lockContador.lock();
            try {
                this.ordenLlegada = ++contadorOrden;
            } finally {
                lockContador.unlock();
            }
        }
        
        @Override
        public void run() {
            try {
                // Pequeña pausa para simular llegadas casi simultáneas
                Thread.sleep((long) (Math.random() * 100));
                
                if (esColor) {
                    imprimirColor();
                } else {
                    imprimirNegro();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        /**
         * Método para imprimir en blanco y negro respetando orden
         */
        private void imprimirNegro() throws InterruptedException {
            System.out.println("[" + ordenLlegada + "] " + nombre + " solicita impresora B/N");
            
            // El lock garantiza que se respete el orden de llegada
            lockNegro.lock();
            try {
                // Adquirir semáforo
                impresorasNegro.acquire();
                
                System.out.println("    >>> [" + ordenLlegada + "] " + nombre + " está IMPRIMIENDO en B/N");
                
                // Simular tiempo de impresión
                Thread.sleep((long) (1000 + Math.random() * 2000));
                
                System.out.println("    <<< [" + ordenLlegada + "] " + nombre + " TERMINÓ impresión B/N");
                
                // Liberar semáforo
                impresorasNegro.release();
            } finally {
                lockNegro.unlock();
            }
        }
        
        /**
         * Método para imprimir a color respetando orden
         */
        private void imprimirColor() throws InterruptedException {
            System.out.println("[" + ordenLlegada + "] " + nombre + " solicita impresora COLOR");
            
            // El lock garantiza que se respete el orden de llegada
            lockColor.lock();
            try {
                // Adquirir semáforo
                impresorasColor.acquire();
                
                System.out.println("    >>> [" + ordenLlegada + "] " + nombre + " está IMPRIMIENDO en COLOR");
                
                // Simular tiempo de impresión
                Thread.sleep((long) (2000 + Math.random() * 2000));
                
                System.out.println("    <<< [" + ordenLlegada + "] " + nombre + " TERMINÓ impresión COLOR");
                
                // Liberar semáforo
                impresorasColor.release();
            } finally {
                lockColor.unlock();
            }
        }
    }
    
    /**
     * Método principal
     */
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("ESCENARIO 2: CON ORDEN ESTRICTO");
        System.out.println("3 impresoras B/N | 2 impresoras COLOR");
        System.out.println("Las solicitudes se atienden en orden FIFO");
        System.out.println("=================================================\n");
        
        // Crear array de usuarios
        Usuario[] usuarios = new Usuario[12];
        
        // 6 usuarios para impresión en negro
        for (int i = 0; i < 6; i++) {
            usuarios[i] = new Usuario("Usuario_Negro_" + (i + 1), false);
        }
        
        // 6 usuarios para impresión a color
        for (int i = 6; i < 12; i++) {
            usuarios[i] = new Usuario("Usuario_Color_" + (i - 5), true);
        }
        
        // Iniciar todos los hilos
        for (Usuario usuario : usuarios) {
            usuario.start();
        }
        
        // Esperar a que todos terminen
        for (Usuario usuario : usuarios) {
            try {
                usuario.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("\n=================================================");
        System.out.println("TODAS LAS IMPRESIONES HAN FINALIZADO");
        System.out.println("=================================================");
    }
}