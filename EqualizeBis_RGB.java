// Importation des paquets nécessaires. Le plugin n'est pas lui-même un paquet (pas de mot-clé package)
import ij.*; 							// pour classes ImagePlus et IJ
import ij.plugin.filter.PlugInFilter; 	// pour interface PlugInFilter
import ij.process.*; 					// pour classe ImageProcessor
import java.awt.*; 						// pour classe Rectangle

// Nom de la classe = nom du fichier.  Implémente l'interface PlugInFilter
public class EqualizeBis_RGB implements PlugInFilter {
	
	public int setup(String arg, ImagePlus imp) {
		// Accepte tous types d'images, piles d'images et RoIs, même non rectangulaires
		return DOES_8G+DOES_RGB+DOES_STACKS+SUPPORTS_MASKING;
	}

	public void run(ImageProcessor ip) {
 

		Rectangle r = ip.getRoi(); // Région d'intérêt sélectionnée (r.x=r.y=0 si aucune)

		ColorProcessor cp = ip.convertToColorProcessor(); 

		int distrib = 256;

		//on crée des tableaux de taille 256 pour le rouge, le vert et le bleu qui à un niveau de couleur associe le nombre de pixels correspondant
		int histoR[] = new int[distrib];
		int histoG[] = new int[distrib];
		int histoB[] = new int[distrib];
		Color color;
		int I[][] = new int[r.width][r.height];

		//on établit déjà un tableau I où I[x][y] correspond à l'intensité du pixel (x,y)
		//on parcours tous les pixels de r en incrémentant la valeur indexée par son niveau de gris(qu'on a calculé puis stocké dans I)
		//on obtient ainsi l'histogramme de r ainsi qu'un tableau d'intensité de l'image;

		for (int y=r.y; y<(r.y+r.height); y++)
			for (int x=r.x; x<(r.x+r.width); x++){
				color = cp.getColor(x,y);

				histoR[color.getRed()]++;
				histoG[color.getGreen()]++;
				histoB[color.getBlue()]++;
			}

		//pour chaque valeur d'intensité, on évalue la proportion de r ayant une intensité inférieure

		double RR[] = new double[distrib];
		double RG[] = new double[distrib];
		double RB[] = new double[distrib];
		int pR = 0, pG=0, pB=0;

		for(int i=0;i<distrib;i++){

			pR+=histoR[i];
			pG+=histoG[i];
			pB+=histoB[i];

			RR[i] = ((double)pR)/(double)(r.width*r.height);
			RG[i] = ((double)pG)/(double)(r.width*r.height);
			RB[i] = ((double)pB)/(double)(r.width*r.height);

		}

		Color colorT;
		int rgb[] = new int[3];
		//calcul de la transformation
		for (int yt=r.y; yt<(r.y+r.height); yt++)
			for (int xt=r.x; xt<(r.x+r.width); xt++){
				colorT=cp.getColor(xt,yt);
				rgb[0] = (int)(RR[ colorT.getRed() ]*(distrib-1));
				rgb[1] = (int)(RG[ colorT.getGreen() ]*(distrib-1));
				rgb[2] = (int)(RB[ colorT.getBlue() ]*(distrib-1));

				ip.putPixel(xt,yt,rgb);
			}

	}

}
