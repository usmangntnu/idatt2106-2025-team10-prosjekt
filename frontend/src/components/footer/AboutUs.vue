<template>
  <div class="min-h-screen bg-[#E1E5F2] flex flex-col text-gray-800 px-4 py-12">
    <div class="max-w-4xl mx-auto w-full flex-grow">
      <h1 class="text-3xl font-bold mb-6">Om oss</h1>

      <p class="mb-8">
        Plattformen eies og driftes av en gruppe engasjerte utviklere som
        studerer ved NTNU. Dette prosjektet er utviklet som en del av en
        studieoppgave, med mål om å gjøre krisehåndtering enklere og mer
        tilgjengelig for alle.
        <br /><br />
        Har du generelle spørsmål eller henvendelser knyttet til prosjektet, kan
        du kontakte oss på vår felles e-post:
      </p>

      <p class="mb-10 text-lg font-medium">
        📧
        <a
          href="https://mail.google.com/mail/?view=cm&to=krisefikser4@gmail.com"
          target="_blank"
          class="text-blue-600 underline hover:text-blue-800"
        >
          krisefikser4@gmail.com
        </a>
      </p>

      <h2 class="text-2xl font-semibold mb-4">Utviklingsteamet</h2>

      <div class="space-y-8">
        <div v-for="person in personer" :key="person.navn">
          <h3 class="text-xl font-semibold">{{ person.navn }}</h3>
          <p class="text-gray-700">
            Kontakt:
            <a
              :href="`https://outlook.office.com/mail/deeplink/compose?to=${person.email}`"
              target="_blank"
              class="italic text-blue-600 underline hover:text-blue-800"
            >
              {{ person.email }}
            </a>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const personer = [
  { navn: 'Aryan Malekian', email: 'aryanm@stud.ntnu.no' },
  { navn: 'Jonathan Skomsøy Hubertz', email: 'jonathhu@stud.ntnu.no' },
  { navn: 'Mikael Stray Frøyshov', email: 'mikaelsf@stud.ntnu.no' },
  { navn: 'Sander Berge', email: 'sanderrb@stud.ntnu.no' },
  { navn: 'Sander Sandvik Nessa', email: 'sandesn@stud.ntnu.no' },
  { navn: 'Scott du Plessis', email: 'scottld@stud.ntnu.no' },
  { navn: 'Usman Ghafoorzai', email: 'usmang@stud.ntnu.no' },
]
</script>
