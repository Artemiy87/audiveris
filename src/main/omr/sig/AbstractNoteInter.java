//----------------------------------------------------------------------------//
//                                                                            //
//                      A b s t r a c t N o t e I n t e r                     //
//                                                                            //
//----------------------------------------------------------------------------//
// <editor-fold defaultstate="collapsed" desc="hdr">
//  Copyright © Herve Bitteur and others 2000-2013. All rights reserved.
//  This software is released under the GNU General Public License.
//  Goto http://kenai.com/projects/audiveris to report bugs or suggestions.
//----------------------------------------------------------------------------//
// </editor-fold>
package omr.sig;

import omr.constant.Constant;
import omr.constant.ConstantSet;

import omr.glyph.Shape;
import omr.glyph.facets.Glyph;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 * Class {@code AbstractNoteInter} is an abstract base for heads and
 * notes interpretations.
 *
 * @author Hervé Bitteur
 */
public class AbstractNoteInter
        extends AbstractInter
{
    //~ Static fields/initializers ---------------------------------------------

    private static final Constants constants = new Constants();

    //~ Instance fields --------------------------------------------------------
    /** Pitch step. */
    protected final int pitch;

    //~ Constructors -----------------------------------------------------------
    /**
     * Creates a new AbstractNoteInter object.
     *
     * @param box   the object bounds
     * @param shape the underlying shape
     * @param grade the inter intrinsic grade
     * @param pitch the note pitch
     */
    public AbstractNoteInter (Rectangle box,
                              Shape shape,
                              double grade,
                              int pitch)
    {
        super(box, shape, grade);
        this.pitch = pitch;
    }

    /**
     * Creates a new AbstractNoteInter object.
     *
     * @param glyph   the underlying glyph
     * @param shape   the related shape
     * @param impacts the grade details
     * @param pitch   the note pitch
     */
    public AbstractNoteInter (Glyph glyph,
                              Shape shape,
                              GradeImpacts impacts,
                              int pitch)
    {
        super(glyph, shape, impacts.getGrade());
        this.setImpacts(impacts);
        this.pitch = pitch;
    }

    //~ Methods ----------------------------------------------------------------
    //---------------//
    // getCoreBounds //
    //---------------//
    @Override
    public Rectangle2D getCoreBounds ()
    {
        return shrink(getBounds());
    }

    //--------------------//
    // getShrinkHoriRatio //
    //--------------------//
    public static double getShrinkHoriRatio ()
    {
        return constants.shrinkHoriRatio.getValue();
    }

    //--------------------//
    // getShrinkVertRatio //
    //--------------------//
    public static double getShrinkVertRatio ()
    {
        return constants.shrinkVertRatio.getValue();
    }

    //--------//
    // shrink //
    //--------//
    /**
     * Shrink a bit a bounding box when checking for note overlap.
     *
     * @param box the bounding box
     * @return the shrunk box
     */
    public static Rectangle2D shrink (Rectangle box)
    {
        double newWidth = constants.shrinkHoriRatio.getValue() * box.width;
        double newHeight = constants.shrinkVertRatio.getValue() * box.height;

        return new Rectangle2D.Double(
                box.getCenterX() - (newWidth / 2.0),
                box.getCenterY() - (newHeight / 2.0),
                newWidth,
                newHeight);
    }

    //----------//
    // getPitch //
    //----------//
    /**
     * @return the pitch
     */
    public int getPitch ()
    {
        return pitch;
    }

    //~ Inner Classes ----------------------------------------------------------
    //-----------//
    // Constants //
    //-----------//
    private static final class Constants
            extends ConstantSet
    {
        //~ Instance fields ----------------------------------------------------

        final Constant.Ratio shrinkHoriRatio = new Constant.Ratio(
                0.5, //0.7,
                "Horizontal shrink ratio to apply when checking note overlap");

        final Constant.Ratio shrinkVertRatio = new Constant.Ratio(
                0.5,
                "Vertical shrink ratio to apply when checking note overlap");

    }
}