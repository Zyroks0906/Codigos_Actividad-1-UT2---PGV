import java.util.concurrent.Semaphore;

/**
 * Escenario 1: Cola de Impresión SIN orden estricto
 * Los usuarios acceden a las impresoras disponibles sin respetar orden de llegada
 * 
 * @author AlejandroMejiasRamirez
 */
public class ImpresorasSinOrden {
    
    // Semáforos para controlar el acceso a las impresoras
    private static Semaphore impresorasNegro = new Semaphore(3); // 3 impresoras B/N
    private static Semaphore impresorasColor = new Semaphore(2);  // 2 impresoras color
    
    /**
     * Clase que representa un usuario que necesita imprimir
     */
    static class Usuario extends Thread {
        private String nombre;
        private boolean esColor;
        
        public Usuario(String nombre, boolean esColor) {
            this.nombre = nombre;
            this.esColor = esColor;
        }
        
        @Override
        public void run() {
            try {
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
         * Método para imprimir en blanco y negro
         */
        private void imprimirNegro() throws InterruptedException {
            System.out.println(nombre + " solicita impresora B/N...");
            
            // Adquirir semáforo (esperar si no hay impresoras disponibles)
            impresorasNegro.acquire();
            
            System.out.println(">>> " + nombre + " está IMPRIMIENDO en B/N");
            
            // Simular tiempo de impresión (1-3 segundos)
            Thread.sleep((long) (1000 + Math.random() * 2000));
            
            System.out.println("<<< " + nombre + " ha TERMINADO de imprimir en B/N");
            
            // Liberar semáforo (impresora disponible de nuevo)
            impresorasNegro.release();
        }
        
        /**
         * Método para imprimir a color
         */
        private void imprimirColor() throws InterruptedException {
            System.out.println(nombre + " solicita impresora COLOR...");
            
            // Adquirir semáforo (esperar si no hay impresoras disponibles)
            impresorasColor.acquire();
            
            System.out.println(">>> " + nombre + " está IMPRIMIENDO en COLOR");
            
            // Simular tiempo de impresión (2-4 segundos, color tarda más)
            Thread.sleep((long) (2000 + Math.random() * 2000));
            
            System.out.println("<<< " + nombre + " ha TERMINADO de imprimir en COLOR");
            
            // Liberar semáforo (impresora disponible de nuevo)
            impresorasColor.release();
        }
    }
    
    /**
     * Método principal
     */
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("ESCENARIO 1: SIN ORDEN ESTRICTO");
        System.out.println("3 impresoras B/N | 2 impresoras COLOR");
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
        
        // Iniciar todos los hilos simultáneamente
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