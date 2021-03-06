package scratchlib.project;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class ScratchVersionTest
{
    @Test
    public void hasCorrectHeaders()
    {
        assertSame("ScratchV02", ScratchVersion.SCRATCH14.getHeader());
        assertSame("BloxExpV01", ScratchVersion.BYOB311.getHeader());
    }

    @Test
    public void looksUpCorrectVersionsFromHeader()
    {
        assertSame(ScratchVersion.SCRATCH14, ScratchVersion.lookupHeader("ScratchV02"));
        assertSame(ScratchVersion.BYOB311, ScratchVersion.lookupHeader("BloxExpV01"));
    }

    @Test
    public void returnsNullForUnknownHeader()
    {
        assertNull(ScratchVersion.lookupHeader("UnknownHdr"));
    }
}
