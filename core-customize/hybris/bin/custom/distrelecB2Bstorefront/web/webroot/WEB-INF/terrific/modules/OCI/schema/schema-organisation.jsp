<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${currentLanguage.isocode eq 'en'}">
    <script type="application/ld+json">
        {
           "@context":"http://schema.org",
           "@type":"Organization",
           "name":"Distrelec",
           "legalName":"Distrelec Group AG",
           "url":"https://www.distrelec.ch",
           "logo":"https://www.distrelec.ch/medias/distrelec-webshop-logo.jpg?context=bWFzdGVyfHJvb3R8MTc2OTd8aW1hZ2UvanBlZ3xoMTEvaDU3LzkxNTMzMDU2NzM3NTguanBnfDZhZjBlM2I1Zjg2NWIxODcyYzMwMjhjMTg0YzAxODk4YjcxMGNiM2E3YmMxMjFhZjdlY2YzNDcyZDlhYTFhN2M",
           "description":"The Distrelec Schweiz AG is a leading High Service Distributor for electronics, automation, and measuring equipment and tools.",
           "foundingDate":"1973",
           "founder":{
              "@type":"Person",
              "name":"Adolf Dätwyler"
           },
           "address":{
              "@type":"PostalAddress",
              "streetAddress":"Grabenstrasse 6",
              "addressLocality":"Nänikon",
              "addressRegion":"Canton of Zürich",
              "postalCode":"8606",
              "addressCountry":"Switzerland"
           },
           "sameAs":[
              "https://www.facebook.com/Distrelec",
              "https://twitter.com/distrelec",
              "https://www.linkedin.com/company/distrelec-distrelec-schuricht-and-elfa-distrelec_",
              "https://plus.google.com/+DistrelecGroup"
           ]
        }
    </script>
</c:if>
<c:if test="${siteUid eq 'distrelec_FR'}">
    <script type="application/ld+json">
        {
            "@context":"http://schema.org",
            "@type":"Organization",
            "name":"Distrelec France",
            "legalName":"Distrelec Schweiz AG",
            "url":"https://www.distrelec.fr",
            "logo":"https://www.distrelec.fr//medias/?context=bWFzdGVyfHJvb3R8NjgzMXxpbWFnZS9wbmd8aDdkLzk0NTAzMjIyMzEzMjYucG5nfGQxMTQ3Njk5YjM1NDFmM2M4ODVjZDNmZTgwZmNlYzNjZTFkMDZkZTllNzZkMjhmZmRhOGI3NjgwYTJkY2ZkYzM",
            "description": "Distrelec est un fournisseur en ligne de matériel 'électronique, d'automatisation, d'outillage et de technologie de mesure.",
            "foundingDate":"1973",
            "founder":{
                "@type":"Person",
                "name":"Adolf Dätwyler"
            },
            "address":{
                "@type":"PostalAddress",
                "streetAddress":"Grabenstrasse 6",
                "addressLocality":"Nänikon",
                "addressRegion":"Canton of Zürich",
                "postalCode":"8606",
                "addressCountry":"Switzerland"
            },
            "sameAs":[
                "https://www.facebook.com/Distrelec/",
                "https://twitter.com/Distrelec",
                "https://www.linkedin.com/company/31416926/admin/",
                "https://www.youtube.com/channel/UC5enNWCDCaCFdQlRpD8Y4eg?view_as=subscriber"
            ]
        }
    </script>
</c:if>