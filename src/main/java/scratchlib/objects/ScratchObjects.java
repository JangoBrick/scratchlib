package scratchlib.objects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import scratchlib.objects.fixed.collections.ScratchObjectArray;
import scratchlib.objects.fixed.collections.ScratchObjectDictionary;
import scratchlib.objects.fixed.collections.ScratchObjectIdentityDictionary;
import scratchlib.objects.fixed.collections.ScratchObjectIdentitySet;
import scratchlib.objects.fixed.collections.ScratchObjectOrderedCollection;
import scratchlib.objects.fixed.collections.ScratchObjectSet;
import scratchlib.objects.fixed.colors.ScratchObjectColor;
import scratchlib.objects.fixed.colors.ScratchObjectTranslucentColor;
import scratchlib.objects.fixed.data.ScratchObjectBitmap;
import scratchlib.objects.fixed.data.ScratchObjectByteArray;
import scratchlib.objects.fixed.data.ScratchObjectSoundBuffer;
import scratchlib.objects.fixed.data.ScratchObjectString;
import scratchlib.objects.fixed.data.ScratchObjectSymbol;
import scratchlib.objects.fixed.data.ScratchObjectUtf8;
import scratchlib.objects.fixed.dimensions.ScratchObjectPoint;
import scratchlib.objects.fixed.dimensions.ScratchObjectRectangle;
import scratchlib.objects.fixed.forms.ScratchObjectColorForm;
import scratchlib.objects.fixed.forms.ScratchObjectForm;
import scratchlib.objects.inline.ScratchObjectBoolean;
import scratchlib.objects.inline.ScratchObjectFloat;
import scratchlib.objects.inline.ScratchObjectLargeNegativeInteger;
import scratchlib.objects.inline.ScratchObjectLargePositiveInteger;
import scratchlib.objects.inline.ScratchObjectSmallInteger;
import scratchlib.objects.inline.ScratchObjectSmallInteger16;
import scratchlib.objects.user.ScratchObjectCustomBlockDefinition;
import scratchlib.objects.user.ScratchObjectVariableFrame;
import scratchlib.objects.user.media.ScratchObjectImageMedia;
import scratchlib.objects.user.media.ScratchObjectSampledSound;
import scratchlib.objects.user.media.ScratchObjectSoundMedia;
import scratchlib.objects.user.morphs.ScratchObjectListMorph;
import scratchlib.objects.user.morphs.ScratchObjectMorph;
import scratchlib.objects.user.morphs.ScratchObjectSpriteMorph;
import scratchlib.objects.user.morphs.ScratchObjectStageMorph;
import scratchlib.objects.user.morphs.ui.ScratchObjectAlignmentMorph;
import scratchlib.objects.user.morphs.ui.ScratchObjectImageMorph;
import scratchlib.objects.user.morphs.ui.ScratchObjectSimpleSliderMorph;
import scratchlib.objects.user.morphs.ui.ScratchObjectStringMorph;
import scratchlib.objects.user.morphs.ui.ScratchObjectUpdatingStringMorph;
import scratchlib.objects.user.morphs.ui.ScratchObjectWatcherMorph;
import scratchlib.objects.user.morphs.ui.ScratchObjectWatcherReadoutFrameMorph;
import scratchlib.objects.user.morphs.ui.ScratchObjectWatcherSliderMorph;
import scratchlib.project.ScratchProject;
import scratchlib.reader.ScratchInputStream;


/**
 * Allows class lookup by ID.
 */
public class ScratchObjects
{
    private static final List<Supplier<ScratchObject>> cons = new ArrayList<>(Collections.nCopies(255, null));

    /**
     * Stores the given constructor for the given class ID.
     *
     * @param id The class ID.
     * @param con The class constructor.
     */
    private static void storeConstructor(int id, Supplier<ScratchObject> con)
    {
        if (cons.get(id - 1) != null) {
            throw new IllegalArgumentException(String.format("class ID already used: %d", id));
        }
        cons.set(id - 1, con);
    }

    /**
     * Finds the constructor for the given class ID.
     *
     * @param id The class ID.
     * @return The respective class constructor.
     */
    public static Supplier<ScratchObject> lookupConstructor(int id)
    {
        return cons.get(id - 1);
    }

    /**
     * Convenience method for reading an object or reference from the given
     * input stream.
     *
     * @param in The stream to read from.
     * @param project The project reading for.
     * @return The object that was read.
     * @throws IOException
     */
    public static ScratchOptionalField read(ScratchInputStream in, ScratchProject project) throws IOException
    {
        final int id = in.read();

        if (id == 99) {
            return new ScratchOptionalField(in.read24bitUnsignedInt());
        }

        Supplier<ScratchObject> con = lookupConstructor(id);
        if (con == null) {
            throw new IOException(String.format("unknown class id: %d", id));
        }

        ScratchObject obj = con.get();
        obj.readFrom(id, in, project);

        return new ScratchOptionalField(obj);
    }

    static {
        // inline: constants (1, 2, 3)
        storeConstructor(ScratchObject.NIL.getClassID(), () -> ScratchObject.NIL);
        storeConstructor(ScratchObjectBoolean.TRUE.getClassID(), () -> ScratchObjectBoolean.TRUE);
        storeConstructor(ScratchObjectBoolean.FALSE.getClassID(), () -> ScratchObjectBoolean.FALSE);

        // inline: numbers (4 ... 8)
        storeConstructor(ScratchObjectSmallInteger.CLASS_ID, ScratchObjectSmallInteger::new);
        storeConstructor(ScratchObjectSmallInteger16.CLASS_ID, ScratchObjectSmallInteger16::new);
        storeConstructor(ScratchObjectLargePositiveInteger.CLASS_ID, ScratchObjectLargePositiveInteger::new);
        storeConstructor(ScratchObjectLargeNegativeInteger.CLASS_ID, ScratchObjectLargeNegativeInteger::new);
        storeConstructor(ScratchObjectFloat.CLASS_ID, ScratchObjectFloat::new);

        // fixed-format: data (9 ... 14)
        storeConstructor(ScratchObjectString.CLASS_ID, ScratchObjectString::new);
        storeConstructor(ScratchObjectSymbol.CLASS_ID, ScratchObjectSymbol::new);
        storeConstructor(ScratchObjectByteArray.CLASS_ID, ScratchObjectByteArray::new);
        storeConstructor(ScratchObjectSoundBuffer.CLASS_ID, ScratchObjectSoundBuffer::new);
        storeConstructor(ScratchObjectBitmap.CLASS_ID, ScratchObjectBitmap::new);
        storeConstructor(ScratchObjectUtf8.CLASS_ID, ScratchObjectUtf8::new);

        // fixed-format: collections (20 ... 25)
        storeConstructor(ScratchObjectArray.CLASS_ID, ScratchObjectArray::new);
        storeConstructor(ScratchObjectOrderedCollection.CLASS_ID, ScratchObjectOrderedCollection::new);
        storeConstructor(ScratchObjectSet.CLASS_ID, ScratchObjectSet::new);
        storeConstructor(ScratchObjectIdentitySet.CLASS_ID, ScratchObjectIdentitySet::new);
        storeConstructor(ScratchObjectDictionary.CLASS_ID, ScratchObjectDictionary::new);
        storeConstructor(ScratchObjectIdentityDictionary.CLASS_ID, ScratchObjectIdentityDictionary::new);

        // fixed-format: colors (30, 31)
        storeConstructor(ScratchObjectColor.CLASS_ID, ScratchObjectColor::new);
        storeConstructor(ScratchObjectTranslucentColor.CLASS_ID, ScratchObjectTranslucentColor::new);

        // fixed-format: dimensions (32, 33)
        storeConstructor(ScratchObjectPoint.CLASS_ID, ScratchObjectPoint::new);
        storeConstructor(ScratchObjectRectangle.CLASS_ID, ScratchObjectRectangle::new);

        // fixed-format: forms
        storeConstructor(ScratchObjectForm.CLASS_ID, ScratchObjectForm::new);
        storeConstructor(ScratchObjectColorForm.CLASS_ID, ScratchObjectColorForm::new);

        // ---- USER-CLASS OBJECTS

        storeConstructor(ScratchObjectMorph.CLASS_ID, ScratchObjectMorph::new);

        // morphs
        storeConstructor(ScratchObjectSpriteMorph.CLASS_ID, ScratchObjectSpriteMorph::new);
        storeConstructor(ScratchObjectStageMorph.CLASS_ID, ScratchObjectStageMorph::new);
        storeConstructor(ScratchObjectListMorph.CLASS_ID, ScratchObjectListMorph::new);

        // media
        storeConstructor(ScratchObjectSampledSound.CLASS_ID, ScratchObjectSampledSound::new);
        storeConstructor(ScratchObjectImageMedia.CLASS_ID, ScratchObjectImageMedia::new);
        storeConstructor(ScratchObjectSoundMedia.CLASS_ID, ScratchObjectSoundMedia::new);

        // ui
        storeConstructor(ScratchObjectAlignmentMorph.CLASS_ID, ScratchObjectAlignmentMorph::new);
        storeConstructor(ScratchObjectStringMorph.CLASS_ID, ScratchObjectStringMorph::new);
        storeConstructor(ScratchObjectUpdatingStringMorph.CLASS_ID, ScratchObjectUpdatingStringMorph::new);
        storeConstructor(ScratchObjectSimpleSliderMorph.CLASS_ID, ScratchObjectSimpleSliderMorph::new);
        storeConstructor(ScratchObjectImageMorph.CLASS_ID, ScratchObjectImageMorph::new);

        // ui: watcher
        storeConstructor(ScratchObjectWatcherMorph.CLASS_ID, ScratchObjectWatcherMorph::new);
        storeConstructor(ScratchObjectWatcherReadoutFrameMorph.CLASS_ID, ScratchObjectWatcherReadoutFrameMorph::new);
        storeConstructor(ScratchObjectWatcherSliderMorph.CLASS_ID, ScratchObjectWatcherSliderMorph::new);

        // BYOB
        storeConstructor(ScratchObjectCustomBlockDefinition.CLASS_ID, ScratchObjectCustomBlockDefinition::new);
        storeConstructor(ScratchObjectVariableFrame.CLASS_ID, ScratchObjectVariableFrame::new);
    }
}
