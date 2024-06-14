import { of } from 'rxjs';
import { Page } from '@spartacus/core';
import { PageCategory } from '@features/tracking/page-category.service';

export const mockProductCategoryService = {
  getCurrentCategoryData: () =>
    of({
      breadcrumbs: [
        {
          code: 'cat-L1D_379523',
          images: [
            {
              key: 'landscape_large',
              value: {
                format: 'landscape_large',
                url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/8-/01/HP-elite-x2-30044408-01.jpg',
              },
            },
            {
              key: 'landscape_medium',
              value: {
                format: 'landscape_medium',
                url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_medium/8-/01/HP-elite-x2-30044408-01.jpg',
              },
            },
            {
              key: 'landscape_small',
              value: {
                format: 'landscape_small',
                url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_small/8-/01/HP-elite-x2-30044408-01.jpg',
              },
            },
            {
              key: 'portrait_small',
              value: {
                format: 'portrait_small',
                url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/8-/01/HP-elite-x2-30044408-01.jpg',
              },
            },
            {
              key: 'portrait_medium',
              value: {
                format: 'portrait_medium',
                url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/8-/01/HP-elite-x2-30044408-01.jpg',
              },
            },
          ],
          introText:
            'Distrelec offer a huge range of office and domestic computing supplies, with a large number of products in the following areas:',
          level: 1,
          name: 'Office, Computing & Network Products',
          nameEN: 'Office, Computing & Network Products',
          productCount: 0,
          selected: false,
          seoMetaDescription:
            'Office, Computing & Network Products order online from  Distrelec Switzerland:  ✓ Free shipping from 50€  ✓ over 150,000 products in stock  ✓ Pay per invoice.',
          seoMetaTitle: 'Office, Computing & Network Products|$(siteName)',
          seoSections: [
            {
              header: 'Office, Computing & Network Products',
              text: "• Printing: 2D & 3D printers including an array of ink supplies and paper <br/>• Visual entertainment: TVs, projectors, projection screens and supporting cables <br/>• Travel: GPS systems, both hand-held and car-mountable devices. We also feature travel packs, holders and charging cables <br/>• Software: CAD and graphics software, Microsoft applications and operating systems all make up the software section of our website <br/>• Photography: We have an assortment of compact and action cameras for capturing the best quality photos when outdoors on the go or projects that need that little extra precision <br/>• Computing: If you’re building or maintaining the hardware of your computer you need to check out our range of storage drives, power supplies and other computer components <br/>• Office furniture and supplies: Chairs, cleaning products, calculators and other office supplies can all be found here. When moving onto a new desk or renovating a new office you will always need those essential supplies <br/>• Organisation: Whether it's draw filing systems, transport storage or protecting casing, Distrelec have a wide variety of organisational products to suit your every need in keeping all your business or personal belongings in a need and tidy place.",
            },
            {
              header: 'Why should you choose Distrelec for computing and peripherals?',
              text: 'Distrelec offer an extensive range of computing products. Our products are all made by leading brands in the industry and offer some of the highest-quality products at competitive prices. Whether it’s a simple mouse and keyboard set or a 3D printer, Distrelec are an experienced company with a strong team of technical experts who can provide you with the best products. Then once you have purchased your ideal computing device you can talk with a member of our team who will provide the best possible advice in installing and using your product.',
            },
          ],
          url: '/office-computing-network-products/c/cat-L1D_379523',
        },
      ],
      showCategoriesOnly: false,
      sourceCategory: {
        code: 'cat-DNAV_PL_09010506',
        images: [
          {
            key: 'landscape_large',
            value: {
              format: 'landscape_large',
              url: 'https://pretest.media.distrelec.com/Web/WebShopImages/landscape_large/90/77/telefonkablage_189077.jpg',
            },
          },
          {
            key: 'portrait_small',
            value: {
              format: 'portrait_small',
              url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_small/90/77/telefonkablage_189077.jpg',
            },
          },
          {
            key: 'portrait_medium',
            value: {
              format: 'portrait_medium',
              url: 'https://pretest.media.distrelec.com/Web/WebShopImages/portrait_medium/90/77/telefonkablage_189077.jpg',
            },
          },
        ],
        level: 3,
        name: 'Telephone Cable Assemblies',
        nameEN: 'Telephone Cable Assemblies',
        productCount: 0,
        relatedData: [
          {
            CRelatedData: [
              {
                type: 'RELATED_CATEGORY',
                values: [
                  {
                    count: 742,
                    name: 'Cable Assemblies',
                    order: 0,
                    uid: 'cat-L3D_525395',
                    url: '/en/cable-wire/cable-assemblies/c/cat-L3D_525395',
                  },
                  {
                    count: 683,
                    name: 'Terminal Blocks',
                    order: 1,
                    uid: 'cat-L3D_525365',
                    url: '/en/connectors/terminal-blocks/c/cat-L3D_525365',
                  },
                  {
                    count: 307,
                    name: 'Crimp Terminals & Ferrules',
                    order: 2,
                    uid: 'cat-L3D_525392',
                    url: '/en/connectors/crimp-terminals-ferrules/c/cat-L3D_525392',
                  },
                  {
                    count: 224,
                    name: 'Ring Cable Terminals',
                    order: 3,
                    uid: 'cat-DNAV_090407',
                    url: '/en/connectors/crimp-terminals-ferrules/ring-cable-terminals/c/cat-DNAV_090407',
                  },
                  {
                    count: 210,
                    name: 'Power & Device Cable Assemblies',
                    order: 4,
                    uid: 'cat-DNAV_090111',
                    url: '/en/cable-wire/cable-assemblies/power-device-cable-assemblies/c/cat-DNAV_090111',
                  },
                ],
              },
            ],
            type: 'RELATED_CATEGORY',
          },
          {
            CRelatedData: [
              {
                type: 'RELATED_MANUFACTURER',
                values: [
                  {
                    count: 66,
                    name: 'RND Connect',
                    order: 0,
                    uid: 'man_rnc',
                    url: '/en/manufacturer/rnd-connect/man_rnc',
                  },
                  {
                    count: 19,
                    name: 'Nedis',
                    order: 1,
                    uid: 'man_ned',
                    url: '/en/manufacturer/nedis/man_ned',
                  },
                  {
                    count: 7,
                    name: 'MSL Enterprises Corp',
                    order: 2,
                    uid: 'man_msl',
                    url: '/en/manufacturer/msl-enterprises-corp/man_msl',
                  },
                  {
                    count: 4,
                    name: 'Roline',
                    order: 3,
                    uid: 'man_rle',
                    url: '/en/manufacturer/roline/man_rle',
                  },
                ],
              },
            ],
            type: 'RELATED_MANUFACTURER',
          },
        ],
        selected: false,
        seoMetaDescription:
          'Distrelec Switzerland stocks a wide range of Telephone Cable Assemblies. Next Day Delivery Available, Friendly Expert Advice & Over 180,000 products in stock.',
        seoMetaTitle: 'Telephone Cable Assemblies|$(siteName)',
        seoSections: [],
        url: '/office-computing-network-products/computer-cables-adapters/telephone-cable-assemblies/c/cat-DNAV_PL_09010506',
      },
      subCategories: [],
    }),
  getPageCategory(page: Page, routeParams?: { [key: string]: string }, category?: PageCategory) {
    if (page.type === 'CategoryPage') {
      return PageCategory.Category;
    } else if (page.type === 'ProductPage') {
      return PageCategory.ProductDetails;
    } else {
      return 'Other Category';
    }
  },
};
