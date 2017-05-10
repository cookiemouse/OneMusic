package com.mouse.cookie.onemusic.databean;

/**
 * Created by cookie on 17-5-9.
 */

public class LyricContentBean {
    /**
     * charset : utf8
     * content : WzAwOjAwLjY1XUZ1bi4gLSBXZSBBcmUgWW91bmcgKGZlYXQuIEphbmVsbGUgTW9uw6FlKQ0KWzAwOjA4LjkwXUdpdmUgbWUgYSBzZWNvbmQgSScNClswMDoxMC44NF1JIG5lZWQgdG8gZ2V0IG15IHN0b3J5IHN0cmFpZ2h0DQpbMDA6MTIuOTddTXkgZnJpZW5kcyBhcmUgaW4gdGhlIGJhdGhyb29tDQpbMDA6MTQuMjhdR2V0dGluZyBoaWdoZXIgdGhhbiB0aGUgRW1waXJlIFN0YXRlDQpbMDA6MTYuNzFdTXkgbG92ZXIgc2hlJ3Mgd2FpdGluZyBmb3IgbWUganVzdCBhY3Jvc3MgdGhlIGJhcg0KWzAwOjIwLjU5XU15IHNlYXQncyBiZWVuIHRha2VuIGJ5IHNvbWUgc3VuZ2xhc3Nlcw0KWzAwOjIzLjAzXUFza2luZyBib3V0IGEgc2NhcicgYW5kDQpbMDA6MjUuNDddSSBrbm93IEkgZ2F2ZSBpdCB0byB5b3UgbW9udGhzIGFnbw0KWzAwOjI5LjY1XUkga25vdyB5b3UncmUgdHJ5aW5nIHRvIGZvcmdldA0KWzAwOjMyLjkwXUJ1dCBiZXR3ZWVuIHRoZSBkcmlua3MgYW5kIHN1YnRsZSB0aGluZ3MNClswMDozNS4wOV1UaGUgaG9sZXMgaW4gbXkgYXBvbG9naWVzJyB5b3Uga25vdw0KWzAwOjM5LjY1XUknbSB0cnlpbmcgaGFyZCB0byB0YWtlIGl0IGJhY2sNClswMDo0MS41M11TbyBpZiBieSB0aGUgdGltZSB0aGUgYmFyIGNsb3Nlcw0KWzAwOjQzLjk3XUFuZCB5b3UgZmVlbCBsaWtlIGZhbGxpbmcgZG93bg0KWzAwOjQ2LjY2XUknbGwgY2FycnkgeW91IGhvbWUNClswMDo0OS40N11Ub25pZ2h0DQpbMDA6NTAuNDBdV2UgYXJlIHlvdW5nDQpbMDA6NTcuOTZdU28gbGV0J3Mgc2V0IHRoZSB3b3JsZCBvbiBmaXJlDQpbMDE6MDIuMDJdV2UgY2FuIGJ1cm4gYnJpZ2h0ZXIgdGhhbiB0aGUgc3VuDQpbMDE6MDguOTZdVG9uaWdodA0KWzAxOjE0LjQwXVdlIGFyZSB5b3VuZw0KWzAxOjE4Ljc3XVNvIGxldCdzIHNldCB0aGUgd29ybGQgb24gZmlyZQ0KWzAxOjIyLjg5XVdlIGNhbiBidXJuIGJyaWdodGVyIHRoYW4gdGhlIHN1bg0KWzAxOjMwLjQ2XU5vdyBJIGtub3cgdGhhdCBJJ20gbm90IGFsbCB0aGF0IHlvdSBnb3QNClswMTozNi41Ml1JIGd1ZXNzIHRoYXQgSScgSSBqdXN0IHRob3VnaHQNClswMTozOS4yN11NYXliZSB3ZSBjb3VsZCBmaW5kIG5ldyB3YXlzIHRvIGZhbGwgYXBhcnQNClswMTo0Mi4yMV1CdXQgb3VyIGZyaWVuZHMgYXJlIGJhY2sNClswMTo0NC4wOF1TbyBsZXQncyByYWlzZSBhIGN1cA0KWzAxOjQ3LjE0XSdDYXVzZSBJIGZvdW5kIHNvbWVvbmUgdG8gY2FycnkgbWUgaG9tZQ0KWzAxOjUxLjQwXVRvbmlnaHQNClswMTo1NS4yN11XZSBhcmUgeW91bmcNClswMTo1OS43N11TbyBsZXQncyBzZXQgdGhlIHdvcmxkIG9uIGZpcmUNClswMjowMy45Nl1XZSBjYW4gYnVybiBicmlnaHRlciB0aGFuIHRoZSBzdW4NClswMjoxMC45MF1Ub25pZ2h0DQpbMDI6MTYuMDhdV2UgYXJlIHlvdW5nDQpbMDI6MjAuMDJdU28gbGV0J3Mgc2V0IHRoZSB3b3JsZCBvbiBmaXJlDQpbMDI6MjQuNThdV2UgY2FuIGJ1cm4gYnJpZ2h0ZXIgdGhhbiB0aGUgc3VuDQpbMDI6MzIuNzddQ2FycnkgbWUgaG9tZSB0b25pZ2h0IChOYW5hbmFuYW5hbmEpDQpbMDI6MzQuNzddSnVzdCBjYXJyeSBtZSBob21lIHRvbmlnaHQgKE5hbmFuYW5hbmFuYSkNClswMjo0My40Nl1DYXJyeSBtZSBob21lIHRvbmlnaHQgKE5hbmFuYW5hbmFuYSkNClswMjo0OC45MF1KdXN0IGNhcnJ5IG1lIGhvbWUgdG9uaWdodCAoTmFuYW5hbmFuYW5hKQ0KWzAyOjU1LjE1XVRoZSB3b3JsZCBpcyBvbiBteSBzaWRlDQpbMDI6NTcuNTJdSSBoYXZlIG5vIHJlYXNvbiB0byBydW4NClswMzowMC4wMl1TbyB3aWxsIHNvbWVvbmUgY29tZSBhbmQgY2FycnkgbWUgaG9tZSB0b25pZ2h0DQpbMDM6MDUuNDZdVGhlIGFuZ2VscyBuZXZlciBhcnJpdmVkDQpbMDM6MDguMTVdQnV0IEkgY2FuIGhlYXIgdGhlIGNob2lyDQpbMDM6MTAuMDJdU28gd2lsbCBzb21lb25lIGNvbWUgYW5kIGNhcnJ5IG1lIGhvbWUNClswMzoxNC45MF1Ub25pZ2h0DQpbMDM6MTguNzFdV2UgYXJlIHlvdW5nDQpbMDM6MjMuMjddU28gbGV0J3Mgc2V0IHRoZSB3b3JsZCBvbiBmaXJlDQpbMDM6MjcuMjFdV2UgY2FuIGJ1cm4gYnJpZ2h0ZXIgdGhhbiB0aGUgc3VuDQpbMDM6MzQuMzNdVG9uaWdodA0KWzAzOjM5LjY1XVdlIGFyZSB5b3VuZw0KWzAzOjQzLjQ2XVNvIGxldCdzIHNldCB0aGUgd29ybGQgb24gZmlyZQ0KWzAzOjQ4LjA5XVdlIGNhbiBidXJuIGJyaWdodGVyIHRoYW4gdGhlIHN1bg0KWzAzOjU1LjI3XVNvIGlmIGJ5IHRoZSB0aW1lIHRoZSBiYXIgY2xvc2VzDQpbMDM6NTcuNzddQW5kIHlvdSBmZWVsIGxpa2UgZmFsbGluZyBkb3duDQpbMDQ6MDIuODNdSSdsbCBjYXJyeSB5b3UgaG9tZSB0b25pZ2h0DQpbMDQ6MDYuMDldRW5kDQo=
     * fmt : lrc
     * info : OK
     * status : 200
     */

    private String charset;
    private String content;
    private String fmt;
    private String info;
    private int status;

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFmt() {
        return fmt;
    }

    public void setFmt(String fmt) {
        this.fmt = fmt;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
